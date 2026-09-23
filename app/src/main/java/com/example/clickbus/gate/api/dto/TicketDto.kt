package com.example.clickbus.gate.api.dto

data class ViagemResumoDto(
    val origem: String,
    val destino: String
)

data class TicketDto(
    val codigoQr: String,
    val poltrona: String?,
    val viagem: ViagemResumoDto?
)