package com.clickbus.gate.api.service;

import com.clickbus.gate.api.controller.TicketRequest;
import com.clickbus.gate.api.controller.ValidacaoResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class MotorDeRegrasService {

    public ValidacaoResponse aplicar(TicketRequest req) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime embarque = LocalDateTime.parse(req.getDataHoraEmbarque());
        int tempoRestante = (int) ChronoUnit.MINUTES.between(agora, embarque);

        if (tempoRestante < 0) {
            return new ValidacaoResponse(
                "REALOCACAO",
                "Ônibus partiu há " + Math.abs(tempoRestante) + " min. Verificando realocação...",
                0xFFE24B4AL,
                agora.toString()
            );
        }
        if (tempoRestante > 60) {
            return new ValidacaoResponse(
                "AREA_DESCANSO",
                "Você está " + tempoRestante + " min adiantado. Dirija-se à área de descanso.",
                0xFF378ADDL,
                agora.toString()
            );
        }
        if ("CHEIA".equals(req.getOcupacao())) {
            return new ValidacaoResponse(
                "AREA_DESCANSO",
                "Plataforma " + req.getPlataforma() + " cheia. Aguarde " + tempoRestante + " min.",
                0xFFBA7517L,
                agora.toString()
            );
        }
        return new ValidacaoResponse(
            "EMBARQUE_IMEDIATO",
            "Plataforma " + req.getPlataforma() + " liberada. Embarque imediato!",
            0xFF639922L,
            agora.toString()
        );
    }
}
