package com.example.clickbus.gate.notificacao

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class EmbarqueWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val titulo = inputData.getString(CHAVE_TITULO) ?: return Result.failure()
        val texto = inputData.getString(CHAVE_TEXTO) ?: return Result.failure()
        val id = inputData.getInt(CHAVE_ID, 0)

        NotificacaoHelper.mostrar(applicationContext, id, titulo, texto)
        return Result.success()
    }

    companion object {
        const val CHAVE_TITULO = "titulo"
        const val CHAVE_TEXTO = "texto"
        const val CHAVE_ID = "id"
    }
}