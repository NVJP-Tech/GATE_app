package com.example.clickbus.gate.api.dto

data class ValidacaoRequest(
    val codigoQr: String,
    val canalValidacao: String, // "GATE_QR" ou "APP_GPS"
    val gateId: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)