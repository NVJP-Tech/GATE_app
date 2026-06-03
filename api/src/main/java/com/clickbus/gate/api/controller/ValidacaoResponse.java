package com.clickbus.gate.api.controller;

public class ValidacaoResponse {
    private String resultadoTriagem;
    private String mensagem;
    private Long corIndicador;
    private String dataHoraValidacao;

    public ValidacaoResponse() {}

    public ValidacaoResponse(String resultadoTriagem, String mensagem, Long corIndicador, String dataHoraValidacao) {
        this.resultadoTriagem = resultadoTriagem;
        this.mensagem = mensagem;
        this.corIndicador = corIndicador;
        this.dataHoraValidacao = dataHoraValidacao;
    }

    public String getResultadoTriagem() { return resultadoTriagem; }
    public void setResultadoTriagem(String resultadoTriagem) { this.resultadoTriagem = resultadoTriagem; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public Long getCorIndicador() { return corIndicador; }
    public void setCorIndicador(Long corIndicador) { this.corIndicador = corIndicador; }
    public String getDataHoraValidacao() { return dataHoraValidacao; }
    public void setDataHoraValidacao(String dataHoraValidacao) { this.dataHoraValidacao = dataHoraValidacao; }
}
