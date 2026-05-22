package com.example.clickbus.gate

data class ContextoGate (
    val ticket: Ticket,
    val tempoRestante: Int,
    val horarioAtual: Int,
    val ocupacaoPlataforma: OcupacaoPlataforma = ticket.plataforma.ocupacao
)