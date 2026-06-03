package com.example.clickbus.gate.regras

import com.example.clickbus.gate.modelo.ContextoGate
import com.example.clickbus.gate.modelo.OcupacaoPlataforma

enum class ResultadoTriagem(val titulo: String) {
    AREA_DESCANSO("Área de Descanso"),
    EMBARQUE_IMEDIATO("Embarque Imediato"),
    REALOCACAO("Realocação")
}

data class DecisaoGate(
    val resultado: ResultadoTriagem,
    val mensagem: String,
    val corIndicador: Long
)

private data class Regra(
    val descricao: String,
    val condicao: (ContextoGate) -> Boolean,
    val decisao: (ContextoGate) -> DecisaoGate
)

private val matrizDeRegras: List<Regra> = listOf(

    Regra(
        descricao = "Passageiro atrasado",
        condicao = { ctx -> ctx.tempoRestante < 0 },
        decisao = { ctx ->
            DecisaoGate(
                resultado = ResultadoTriagem.REALOCACAO,
                mensagem = "Ônibus partiu há ${Math.abs(ctx.tempoRestante)} min. Verificando realocação...",
                corIndicador = 0xFFE24B4A
            )
        }
    ),

    Regra(
        descricao = "Passageiro muito adiantado",
        condicao = { ctx -> ctx.tempoRestante > 60 },
        decisao = { ctx ->
            DecisaoGate(
                resultado = ResultadoTriagem.AREA_DESCANSO,
                mensagem = "Você está ${ctx.tempoRestante} min adiantado. Dirija-se à área de descanso.",
                corIndicador = 0xFF378ADD
            )
        }
    ),

    Regra(
        descricao = "No horário mas plataforma cheia",
        condicao = { ctx ->
            ctx.tempoRestante in 0..60 &&
                    ctx.ocupacaoPlataforma == OcupacaoPlataforma.CHEIA
        },
        decisao = { ctx ->
            DecisaoGate(
                resultado = ResultadoTriagem.AREA_DESCANSO,
                mensagem = "Plataforma ${ctx.ticket.plataforma.nome} cheia. Aguarde ${ctx.tempoRestante} min na área de descanso.",
                corIndicador = 0xFFBA7517
            )
        }
    ),

    Regra(
        descricao = "No horário e plataforma disponível",
        condicao = { ctx ->
            ctx.tempoRestante in 0..60 &&
                    ctx.ocupacaoPlataforma != OcupacaoPlataforma.CHEIA
        },
        decisao = { ctx ->
            DecisaoGate(
                resultado = ResultadoTriagem.EMBARQUE_IMEDIATO,
                mensagem = "Plataforma ${ctx.ticket.plataforma.nome} liberada. Embarque imediato!",
                corIndicador = 0xFF639922
            )
        }
    )
)

fun aplicarMotorDeRegras(contexto: ContextoGate): DecisaoGate {
    return matrizDeRegras.firstOrNull { it.condicao(contexto) }
        ?.decisao(contexto)
        ?: DecisaoGate(
            resultado = ResultadoTriagem.AREA_DESCANSO,
            mensagem = "Situação não identificada. Procure um atendente.",
            corIndicador = 0xFF888780
        )
}
