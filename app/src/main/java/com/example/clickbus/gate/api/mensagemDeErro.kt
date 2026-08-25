package com.example.clickbus.gate.api

import com.example.clickbus.gate.api.dto.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

/**
 * Extrai a mensagem de erro do corpo da resposta (formato ErrorResponse
 * que o GlobalExceptionHandler da API sempre devolve), com um fallback
 * generico caso o corpo nao venha no formato esperado.
 */
fun HttpException.mensagemDeErro(): String {
    return try {
        val corpo = response()?.errorBody()?.string()
        val erro = Gson().fromJson(corpo, ErrorResponse::class.java)
        erro?.mensagem ?: "Erro ao validar o ticket."
    } catch (e: Exception) {
        "Erro ao validar o ticket (HTTP ${code()})."
    }
}