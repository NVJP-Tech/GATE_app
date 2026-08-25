package com.example.clickbus.gate.api.dto

data class ErrorResponse(
    val timestamp: String?,
    val status: Int,
    val erro: String,
    val mensagem: String
)
