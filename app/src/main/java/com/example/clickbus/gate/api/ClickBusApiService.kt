package com.example.clickbus.gate.api

import com.example.clickbus.gate.api.dto.RemarcacaoResponse
import com.example.clickbus.gate.api.dto.ValidacaoRequest
import com.example.clickbus.gate.api.dto.ValidacaoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ClickBusApiService {

    @POST("validacoes")
    suspend fun validar(@Body request: ValidacaoRequest): ValidacaoResponse

    @GET("remarcacoes/pendentes")
    suspend fun listarRemarcacoesPendentes(): List<RemarcacaoResponse>

}