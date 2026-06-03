package com.example.clickbus.gate.modelo

data class Usuario(
    val id: String,
    val nome: String
)

val usuarioLogado = Usuario(
    id = "USR-001",
    nome = "João Silva"
)
