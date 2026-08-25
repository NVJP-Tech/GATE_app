package com.example.clickbus.gate.validacao

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.clickbus.gate.api.RetrofitClient
import com.example.clickbus.gate.api.dto.ValidacaoRequest
import com.example.clickbus.gate.api.dto.ValidacaoResponse

private const val GATE_ID_PADRAO = 1L

suspend fun validarTicket(
    context: Context,
    codigoQr: String,
    canalAppGps: Boolean,
    solicitarPermissaoLocalizacao: () -> Unit
): ValidacaoResponse {
    val request = if (canalAppGps) {
        montarRequestAppGps(context, codigoQr, solicitarPermissaoLocalizacao)
    } else {
        ValidacaoRequest(codigoQr = codigoQr, canalValidacao = "GATE_QR", gateId = GATE_ID_PADRAO)
    }
    return RetrofitClient.api.validar(request)
}

private fun montarRequestAppGps(
    context: Context,
    codigoQr: String,
    solicitarPermissaoLocalizacao: () -> Unit
): ValidacaoRequest {
    val permissaoConcedida = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!permissaoConcedida) {
        solicitarPermissaoLocalizacao()
        return ValidacaoRequest(codigoQr = codigoQr, canalValidacao = "APP_GPS")
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    @Suppress("MissingPermission")
    val localizacao = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

    return ValidacaoRequest(
        codigoQr = codigoQr,
        canalValidacao = "APP_GPS",
        latitude = localizacao?.latitude,
        longitude = localizacao?.longitude
    )
}