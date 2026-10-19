package com.example.clickbus.gate.notificacao

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object NotificacaoAgendador {

    // true = marcos em SEGUNDOS (para testar no emulador). Volte para false antes do pitch/release.
    private const val MODO_TESTE = true

    private val MARCOS_MINUTOS = listOf(60, 15)

    fun agendar(
        context: Context,
        codigoQr: String,
        tempoRestanteMinutos: Int,   // vem do servidor
        plataformaNome: String?      // vem do servidor (pode ser null)
    ) {
        val wm = WorkManager.getInstance(context)
        val unidade = if (MODO_TESTE) TimeUnit.SECONDS else TimeUnit.MINUTES

        // Em modo teste, interpreta o tempo restante em segundos também
        MARCOS_MINUTOS.forEach { marco ->
            val atraso = (tempoRestanteMinutos - marco).toLong()
            val nomeUnico = "embarque-$codigoQr-$marco"

            if (atraso <= 0) {
                wm.cancelUniqueWork(nomeUnico)
                return@forEach
            }

            val local = plataformaNome?.let { "Plataforma $it" } ?: "a plataforma de embarque"
            val titulo = if (marco >= 60) "Seu embarque está se aproximando" else "Embarque em $marco minutos"
            val texto = if (marco >= 60)
                "Faltam cerca de $marco min. Vá para $local."
            else
                "Faltam $marco min. Dirija-se agora para $local."

            val request = OneTimeWorkRequestBuilder<EmbarqueWorker>()
                .setInitialDelay(atraso, unidade)
                .setInputData(
                    workDataOf(
                        EmbarqueWorker.CHAVE_TITULO to titulo,
                        EmbarqueWorker.CHAVE_TEXTO to texto,
                        EmbarqueWorker.CHAVE_ID to (codigoQr.hashCode() + marco)
                    )
                )
                .build()

            // REPLACE evita duplicar se o ticket for validado de novo
            wm.enqueueUniqueWork(nomeUnico, ExistingWorkPolicy.REPLACE, request)
        }
    }

    fun cancelar(context: Context, codigoQr: String) {
        val wm = WorkManager.getInstance(context)
        MARCOS_MINUTOS.forEach { wm.cancelUniqueWork("embarque-$codigoQr-$it") }
    }
}