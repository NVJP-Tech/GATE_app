package com.example.clickbus.gate

enum class OperacaoPlataforma {
    VAZIA, NORMAL, CHEIA
}

data class Plataforma(
    val nome: String,
    val ocupacao: OperacaoPlataforma
)

data class Ticket(
    val passageiro:String,
    val origem: String,
    val destino: String,
    val plataforma: Plataforma,
    val horarioEmbarqueMinutos: Int
)