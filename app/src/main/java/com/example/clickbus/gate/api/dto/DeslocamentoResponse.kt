package com.example.clickbus.gate.api.dto

data class DeslocamentoResponse(
    val distanciaKm: Double,
    val velocidadeUsadaKmh: Double,
    val tempoDeslocamentoMinutos: Int,
    val tempoRestanteMinutos: Int,
    val deslocamentoSuficiente: Boolean,
    val mensagem: String
)