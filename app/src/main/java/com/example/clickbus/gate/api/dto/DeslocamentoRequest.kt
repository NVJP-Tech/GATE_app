package com.example.clickbus.gate.api.dto

data class DeslocamentoRequest(
    val codigoQr: String,
    val latitude: Double,
    val longitude: Double,
    val velocidadeKmh: Double
)