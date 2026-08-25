package com.example.clickbus.gate.api.dto

data class RemarcacaoResponse(
    val id: Long,
    val codigoQrTicketOriginal: String,
    val tipo: String,
    val status: String,
    val motivo: String?,
    val dataHora: String
)