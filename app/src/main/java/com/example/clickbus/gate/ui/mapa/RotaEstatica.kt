package com.example.clickbus.gate.ui.mapa

object RotaEstatica {
    data class Ponto(val x: Float, val y: Float)   // 0f..1f, proporção da tela

    val ENTRADA = Ponto(0.5f, 0.92f)
    private const val CORREDOR_Y = 0.55f
    val PLATAFORMAS = mapOf(
        "A" to Ponto(0.15f, 0.15f), "B" to Ponto(0.38f, 0.15f),
        "C" to Ponto(0.62f, 0.15f), "D" to Ponto(0.85f, 0.15f)
    )

    // Aceita "A" ou "Plataforma A"
    fun normalizar(nome: String) = nome.trim().last().uppercaseChar().toString()

    fun caminho(plataforma: String): List<Ponto> {
        val destino = PLATAFORMAS[plataforma] ?: return emptyList()
        return listOf(ENTRADA, Ponto(ENTRADA.x, CORREDOR_Y), Ponto(destino.x, CORREDOR_Y), destino)
    }

    fun passos(plataforma: String): List<String> {
        val lado = if (plataforma in listOf("A", "B")) "esquerda" else "direita"
        val trecho = if (plataforma in listOf("A", "D"))
            "Siga até o fim do corredor (segunda plataforma à $lado)."
        else
            "Siga até a primeira plataforma à $lado."
        return listOf(
            "Entre pelo portão principal e siga em frente até o corredor central.",
            "No corredor, vire à $lado.",
            trecho,
            "Confirme a letra \"$plataforma\" e aguarde o embarque."
        )
    }
}