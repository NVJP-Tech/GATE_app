package com.example.clickbus.gate.modelo

data class ContextoGate(
    val ticket: Ticket,
    val tempoRestante: Int,
    val ocupacaoPlataforma: OcupacaoPlataforma = ticket.plataforma.ocupacao
)
