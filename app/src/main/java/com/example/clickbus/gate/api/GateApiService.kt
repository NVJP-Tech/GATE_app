package com.example.clickbus.gate.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class TicketRequest(
    val usuarioId: String,
    val passageiro: String,
    val origem: String,
    val destino: String,
    val plataforma: String,
    val dataHoraEmbarque: String,
    val ocupacao: String
)

data class ValidacaoResponse(
    val resultadoTriagem: String,
    val mensagem: String,
    val corIndicador: Long,
    val dataHoraValidacao: String
)

data class HistoricoItem(
    val id: String,
    val origem: String,
    val destino: String,
    val plataforma: String,
    val dataHoraEmbarque: String,
    val dataHoraValidacao: String,
    val resultadoTriagem: String,
    val mensagem: String
)

interface GateApiService {

    @POST("tickets/validar")
    suspend fun validar(@Body request: TicketRequest): ValidacaoResponse

    @GET("tickets/historico/{usuarioId}")
    suspend fun historico(@Path("usuarioId") usuarioId: String): List<HistoricoItem>
}
