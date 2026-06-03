package com.example.clickbus.gate.modelo

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

private val rotas = listOf(
    Pair("São Paulo", "Rio de Janeiro"),
    Pair("Campinas", "Santos"),
    Pair("São Paulo", "Curitiba"),
    Pair("Rio de Janeiro", "Belo Horizonte")
)

private val plataformas = listOf("A", "B", "C", "D")

private fun gerarOcupacaoAleatoria(): OcupacaoPlataforma = when (Random.nextInt(3)) {
    0 -> OcupacaoPlataforma.VAZIA
    1 -> OcupacaoPlataforma.NORMAL
    else -> OcupacaoPlataforma.CHEIA
}

fun gerarTicketAleatorio(usuario: Usuario): Ticket {
    val rota = rotas.random()
    val plataformaNome = plataformas.random()
    val corLinha = when (plataformaNome) {
        "A" -> 0xFFFFD700L
        "B" -> 0xFF378ADDL
        "C" -> 0xFF639922L
        "D" -> 0xFFE24B4AL
        else -> 0xFFFFFFFFL
    }
    val minutosAfrente = (Random.nextInt(1, 32) * 15).toLong()
    val embarque = LocalDateTime.now()
        .plusMinutes(minutosAfrente)
        .withSecond(0)
        .withNano(0)

    return Ticket(
        passageiro = usuario.nome,
        origem = rota.first,
        destino = rota.second,
        plataforma = Plataforma(
            nome = plataformaNome,
            ocupacao = gerarOcupacaoAleatoria(),
            corLinha = corLinha
        ),
        dataHoraEmbarque = embarque
    )
}

private val formatterCard = DateTimeFormatter.ofPattern("dd/MM HH:mm")
private val formatterTopo = DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss")

fun formatarDataHora(dataHora: LocalDateTime): String = dataHora.format(formatterCard)

fun formatarHoraAtual(dataHora: LocalDateTime): String = dataHora.format(formatterTopo)
