package com.example.clickbus.gate.deslocamento

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.clickbus.gate.api.RetrofitClient
import com.example.clickbus.gate.api.dto.DeslocamentoRequest
import com.example.clickbus.gate.notificacao.NotificacaoHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Locale

object MonitorDeslocamento {
    private const val TAG = "MonitorDeslocamento"
    private const val INTERVALO_MS = 10_000L   // 10 s para teste; volte para 30_000L depois
    private const val AMOSTRAS_VELOCIDADE = 5

    private val escopo = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var cliente: FusedLocationProviderClient? = null
    private var callback: LocationCallback? = null
    private val velocidades = ArrayDeque<Double>()
    private var alertado = false
    private var enviando = false

    @SuppressLint("MissingPermission") // checado logo abaixo
    fun iniciar(context: Context, codigoQr: String) {
        parar()
        val ctx = context.applicationContext

        val permitido = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        Log.d(TAG, "iniciar: ticket=$codigoQr localizacao=$permitido notificacao=${NotificacaoHelper.temPermissao(ctx)}")
        if (!permitido) return

        alertado = false
        enviando = false
        velocidades.clear()

        val fused = LocationServices.getFusedLocationProviderClient(ctx)
        val pedido = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVALO_MS)
            .setWaitForAccurateLocation(false)
            .build()
        val cb = object : LocationCallback() {
            override fun onLocationResult(resultado: LocationResult) {
                val local = resultado.lastLocation ?: return
                processar(ctx, codigoQr, local)
            }
        }
        cliente = fused
        callback = cb
        fused.requestLocationUpdates(pedido, cb, Looper.getMainLooper())

        // Avalia já, sem esperar o primeiro intervalo
        fused.lastLocation.addOnSuccessListener { local ->
            if (local != null) processar(ctx, codigoQr, local)
            else Log.d(TAG, "lastLocation = null, aguardando primeira posição")
        }
    }

    fun parar() {
        callback?.let { cliente?.removeLocationUpdates(it) }
        callback = null
        cliente = null
        Log.d(TAG, "parar")
    }

    private fun processar(ctx: Context, codigoQr: String, local: Location) {
        Log.d(TAG, "posição lat=${local.latitude} lng=${local.longitude} vel=${local.speed} m/s")
        val kmh = if (local.hasSpeed()) local.speed * 3.6 else 0.0   // m/s -> km/h
        velocidades.addLast(kmh)
        if (velocidades.size > AMOSTRAS_VELOCIDADE) velocidades.removeFirst()
        avaliar(ctx, codigoQr, local)
    }

    private fun avaliar(ctx: Context, codigoQr: String, local: Location) {
        if (enviando) return
        enviando = true
        val velocidadeMedia = velocidades.average()

        escopo.launch {
            try {
                Log.d(TAG, "chamando API (vel=$velocidadeMedia km/h)")
                val r = RetrofitClient.api.avaliarDeslocamento(
                    DeslocamentoRequest(codigoQr, local.latitude, local.longitude, velocidadeMedia)
                )
                Log.d(TAG, "resposta: suficiente=${r.deslocamentoSuficiente} dist=${r.distanciaKm} km " +
                        "desloc=${r.tempoDeslocamentoMinutos} min restante=${r.tempoRestanteMinutos} min")

                if (!r.deslocamentoSuficiente && !alertado) {
                    alertado = true
                    if (!NotificacaoHelper.temPermissao(ctx)) {
                        Log.w(TAG, "SEM permissão de notificação: o Android vai bloquear o aviso")
                    }
                    Log.d(TAG, "disparando notificação")
                    val ptBr = Locale("pt", "BR")
                    NotificacaoHelper.mostrar(
                        ctx,
                        codigoQr.hashCode() + 99,
                        "Você pode perder o embarque",
                        "Você está a ${"%.1f".format(ptBr, r.distanciaKm)} km (~${r.tempoDeslocamentoMinutos} min), " +
                                "mas o embarque é em ${r.tempoRestanteMinutos} min. Toque para remarcar."
                    )
                } else if (r.deslocamentoSuficiente) {
                    alertado = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao avaliar deslocamento", e)
            } finally {
                enviando = false
            }
        }
    }
}