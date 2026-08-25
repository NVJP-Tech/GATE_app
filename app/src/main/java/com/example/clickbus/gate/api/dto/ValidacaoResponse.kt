package com.example.clickbus.gate.api.dto

data class ValidacaoResponse(
    val resultadoTriagem: String, // AREA_DESCANSO | EMBARQUE_IMEDIATO | REALOCACAO
    val tempoRestanteMinutos: Int,
    val plataformaNumero: String?,
    val statusOcupacao: String?,
    val dataHoraValidacao: String
)