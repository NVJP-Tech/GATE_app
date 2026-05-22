package com.example.clickbus.gate

enum class ResultadoTriagem {
    AREA_DESCANSO,
    EMBARQUE_IMEDIATO,
    REALOCACAO
}

data class DecisaoGate(
    val resultado: ResultadoTriagem,
    val mensagem: String,
    val corIndicador: Long
)

data class Regra(
    val descricao: String,
    val condicao: (Ticket, Int) -> Boolean,
    val decisao: (Ticket, Int) -> DecisaoGate
)

val matrizDeRegras: List<Regra> = listOf(

    Regra(
        descricao = "Passageiro atrasado",
        condicao = { ticket, t -> t < 0 },
        decisao = { ticket, t ->
            DecisaoGate(
                resultado = ResultadoTriagem.REALOCACAO,
                mensagem = "Ônibus partiu há ${Math.abs(t)} min. Verificando realocação...",
                corIndicador = 0xFFE24B4A
            )
        }
    ),

    Regra(
        descricao = "Passageiro muito adiantado",
        condicao = { ticket, t -> t > 60 },
        decisao = { ticket, t ->
            DecisaoGate(
                resultado = ResultadoTriagem.AREA_DESCANSO,
                mensagem = "Você está ${t} min adiantado. Dirija-se à área de descanso.",
                corIndicador = 0xFF378ADD
            )
        }
    ),

    Regra(
        descricao = "No horário mas plataforma cheia",
        condicao = { ticket, t -> t in 0..60 && ticket.plataforma.ocupacao == OcupacaoPlataforma.CHEIA },
        decisao = { ticket, t ->
            DecisaoGate(
                resultado = ResultadoTriagem.AREA_DESCANSO,
                mensagem = "Plataforma ${ticket.plataforma.nome} cheia. Aguarde ${t} min na área de descanso.",
                corIndicador = 0xFFBA7517
            )
        }
    ),

    Regra(
        descricao = "No horário e plataforma disponível",
        condicao = { ticket, t -> t in 0..60 && ticket.plataforma.ocupacao != OcupacaoPlataforma.CHEIA },
        decisao = { ticket, t ->
            DecisaoGate(
                resultado = ResultadoTriagem.EMBARQUE_IMEDIATO,
                mensagem = "Plataforma ${ticket.plataforma.nome} liberada. Embarque imediato!",
                corIndicador = 0xFF639922
            )
        }
    )
)

fun calcularTempoRestante(
    horarioEmbarqueMinutos: Int,
    horarioAtualMinutos: Int
): Int {
    return horarioEmbarqueMinutos - horarioAtualMinutos
}

fun aplicarMotorDeRegras(
    ticket: Ticket,
    horarioAtualMinutos: Int
): DecisaoGate {
    val t = calcularTempoRestante(ticket.horarioEmbarqueMinutos, horarioAtualMinutos)

    val regraAplicada = matrizDeRegras.firstOrNull { regra ->
        regra.condicao(ticket, t)
    }

    return regraAplicada?.decisao(ticket, t) ?: DecisaoGate(
        resultado = ResultadoTriagem.AREA_DESCANSO,
        mensagem = "Situação não identificada. Procure um atendente.",
        corIndicador = 0xFF888780
    )
}