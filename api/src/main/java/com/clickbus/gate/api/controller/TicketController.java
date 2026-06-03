package com.clickbus.gate.api.controller;

import com.clickbus.gate.api.model.TicketHistorico;
import com.clickbus.gate.api.repository.TicketRepository;
import com.clickbus.gate.api.service.MotorDeRegrasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final MotorDeRegrasService motor;
    private final TicketRepository repository;

    public TicketController(MotorDeRegrasService motor, TicketRepository repository) {
        this.motor = motor;
        this.repository = repository;
    }

    @PostMapping("/validar")
    public ResponseEntity<ValidacaoResponse> validar(@RequestBody TicketRequest req) {
        ValidacaoResponse resposta = motor.aplicar(req);

        TicketHistorico historico = new TicketHistorico();
        historico.setUsuarioId(req.getUsuarioId());
        historico.setPassageiro(req.getPassageiro());
        historico.setOrigem(req.getOrigem());
        historico.setDestino(req.getDestino());
        historico.setPlataforma(req.getPlataforma());
        historico.setOcupacao(req.getOcupacao());
        historico.setDataHoraEmbarque(LocalDateTime.parse(req.getDataHoraEmbarque()));
        historico.setDataHoraValidacao(LocalDateTime.parse(resposta.getDataHoraValidacao()));
        historico.setResultadoTriagem(resposta.getResultadoTriagem());
        historico.setMensagem(resposta.getMensagem());
        historico.setCorIndicador(resposta.getCorIndicador());
        repository.save(historico);

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/historico/{usuarioId}")
    public ResponseEntity<List<TicketHistorico>> historico(@PathVariable String usuarioId) {
        return ResponseEntity.ok(
            repository.findByUsuarioIdOrderByDataHoraValidacaoDesc(usuarioId)
        );
    }
}
