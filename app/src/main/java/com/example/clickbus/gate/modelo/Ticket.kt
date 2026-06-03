package com.example.clickbus.gate.modelo

import java.time.LocalDateTime

enum class OcupacaoPlataforma {
    VAZIA, NORMAL, CHEIA
}

data class Plataforma(
    val nome: String,
    val ocupacao: OcupacaoPlataforma,
    val corLinha: Long
)

data class Ticket(
    val passageiro: String,
    val origem: String,
    val destino: String,
    val plataforma: Plataforma,
    val dataHoraEmbarque: LocalDateTime
)
