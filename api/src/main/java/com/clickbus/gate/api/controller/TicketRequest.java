package com.clickbus.gate.api.controller;

public class TicketRequest {
    private String usuarioId;
    private String passageiro;
    private String origem;
    private String destino;
    private String plataforma;
    private String dataHoraEmbarque;
    private String ocupacao;

    public TicketRequest() {}

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
    public String getDataHoraEmbarque() { return dataHoraEmbarque; }
    public void setDataHoraEmbarque(String dataHoraEmbarque) { this.dataHoraEmbarque = dataHoraEmbarque; }
    public String getOcupacao() { return ocupacao; }
    public void setOcupacao(String ocupacao) { this.ocupacao = ocupacao; }
}
