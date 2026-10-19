package com.example.clickbus.gate.api

import com.example.clickbus.gate.api.dto.DeslocamentoRequest
import com.example.clickbus.gate.api.dto.DeslocamentoResponse
import com.example.clickbus.gate.api.dto.RemarcacaoResponse
import com.example.clickbus.gate.api.dto.TicketDto
import com.example.clickbus.gate.api.dto.ValidacaoRequest
import com.example.clickbus.gate.api.dto.ValidacaoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ClickBusApiService {

    @POST("validacoes")
    suspend fun validar(@Body request: ValidacaoRequest): ValidacaoResponse

    @POST("deslocamentos/avaliar")
    suspend fun avaliarDeslocamento(@Body request: DeslocamentoRequest): DeslocamentoResponse

    @GET("remarcacoes/pendentes")
    suspend fun listarRemarcacoesPendentes(): List<RemarcacaoResponse>

    @GET("tickets")
    suspend fun listarTickets(): List<TicketDto>

}