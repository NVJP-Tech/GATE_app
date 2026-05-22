package com.example.clickbus.gate

enum class OcupacaoPlataforma  {
    VAZIA, NORMAL, CHEIA
}

data class Plataforma(
    val nome: String,
    val ocupacao: OcupacaoPlataforma
)

data class Ticket(
    val passageiro:String,
    val origem: String,
    val destino: String,
    val plataforma: Plataforma,
    val horarioEmbarqueMinutos: Int
)