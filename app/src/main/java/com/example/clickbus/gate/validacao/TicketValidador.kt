package com.example.clickbus.gate.validacao

import com.example.clickbus.gate.modelo.ContextoGate
import com.example.clickbus.gate.modelo.Ticket
import com.example.clickbus.gate.regras.DecisaoGate
import com.example.clickbus.gate.regras.aplicarMotorDeRegras
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

fun validarTicket(ticket: Ticket): DecisaoGate {
    val agora = LocalDateTime.now()
    val tempoRestante = ChronoUnit.MINUTES.between(agora, ticket.dataHoraEmbarque).toInt()
    return aplicarMotorDeRegras(ContextoGate(ticket = ticket, tempoRestante = tempoRestante))
}
