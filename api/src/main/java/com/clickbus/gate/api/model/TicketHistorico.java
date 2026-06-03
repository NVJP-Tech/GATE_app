package com.clickbus.gate.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "historico_tickets")
public class TicketHistorico {

    @Id
    private String id;
    private String usuarioId;
    private String passageiro;
    private String origem;
    private String destino;
    private String plataforma;
    private String ocupacao;
    private LocalDateTime dataHoraEmbarque;
    private LocalDateTime dataHoraValidacao;
    private String resultadoTriagem;
    private String mensagem;
    private Long corIndicador;

    public TicketHistorico() {}

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getPassageiro() { return passageiro; }
    public void setPassageiro(String passageiro) { this.passageiro = passageiro; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getPlataforma() { return plataforma; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }

    public String getOcupacao() { return ocupacao; }
    public void setOcupacao(String ocupacao) { this.ocupacao = ocupacao; }

    public LocalDateTime getDataHoraEmbarque() { return dataHoraEmbarque; }
    public void setDataHoraEmbarque(LocalDateTime dataHoraEmbarque) { this.dataHoraEmbarque = dataHoraEmbarque; }

    public LocalDateTime getDataHoraValidacao() { return dataHoraValidacao; }
    public void setDataHoraValidacao(LocalDateTime dataHoraValidacao) { this.dataHoraValidacao = dataHoraValidacao; }

    public String getResultadoTriagem() { return resultadoTriagem; }
    public void setResultadoTriagem(String resultadoTriagem) { this.resultadoTriagem = resultadoTriagem; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public Long getCorIndicador() { return corIndicador; }
    public void setCorIndicador(Long corIndicador) { this.corIndicador = corIndicador; }
}
