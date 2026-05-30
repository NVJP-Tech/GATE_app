package com.example.clickbus.gate

import kotlin.random.Random

val passageiros = listOf(
        "João Silva", "Maria Souza", "Carlos Lima",
        "Ana Paula", "Roberto Costa", "Fernanda Reis"
    )

    val rotas = listOf(
        Pair("São Paulo", "Rio de Janeiro"),
        Pair("Campinas", "Santos"),
        Pair("São Paulo", "Curitiba"),
        Pair("Rio de Janeiro", "Belo Horizonte")
    )

    val plataforma = listOf("A","B","C","D")

fun gerarOcupacaoAleatoria(): OcupacaoPlataforma  {
    return when (Random.nextInt(3)) {
        0 -> OcupacaoPlataforma .VAZIA
        1 -> OcupacaoPlataforma .NORMAL
        else -> OcupacaoPlataforma .CHEIA
    }
}

fun gerarTicketAleatorio(): Ticket {
    val rota = rotas.random()
    val plataformaNome = plataforma.random()
    val ocupacao = gerarOcupacaoAleatoria()

    val hora = Random.nextInt(6,22)
    val minutos = listOf(0,15,30,45).random()
    val embarqueEmMinutos = hora * 60 + minutos

    val corLinha = when (plataformaNome) {
        "A" -> 0xFFFFD700L // Amarela
        "B" -> 0xFF378ADDL // Azul
        "C" -> 0xFF639922L // Verde
        "D" -> 0xFFE24B4AL // Vermelha
        else -> 0xFFFFFFFFL
    }
    return Ticket(
        passageiros.random(),
        origem = rota.first,
        destino = rota.second,
        plataforma = Plataforma(
            nome = plataformaNome,
            ocupacao = ocupacao,
            corLinha = corLinha
        ),
        horarioEmbarqueMinutos = embarqueEmMinutos
    )
}

fun minutosParaHorario(minutos: Int): String {
    val hora = minutos / 60
    val min = minutos % 60
    return "%02d:%02d".format(hora, min)
}