package com.example.clickbus.gate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clickbus.gate.api.dto.ValidacaoResponse
import com.example.clickbus.gate.ui.theme.*

private data class EstiloResultado(
    val cores: List<androidx.compose.ui.graphics.Color>,
    val icone: ImageVector,
    val titulo: String,
    val subtitulo: String
)

private fun estiloPara(resultadoTriagem: String): EstiloResultado = when (resultadoTriagem) {
    "EMBARQUE_IMEDIATO" -> EstiloResultado(
        cores = listOf(SucessoTopo, SucessoMeio, SucessoBase),
        icone = Icons.Filled.CheckCircle,
        titulo = "Embarque Liberado",
        subtitulo = "Dirija-se à plataforma agora"
    )
    "AREA_DESCANSO" -> EstiloResultado(
        cores = listOf(AtencaoTopo, AtencaoMeio, AtencaoBase),
        icone = Icons.Filled.Schedule,
        titulo = "Aguarde um instante",
        subtitulo = "Você tem tempo até o embarque"
    )
    "REALOCACAO" -> EstiloResultado(
        cores = listOf(AlertaTopo, AlertaMeio, AlertaBase),
        icone = Icons.Filled.Warning,
        titulo = "Embarque Perdido",
        subtitulo = "Verifique sua remarcação"
    )
    else -> EstiloResultado(
        cores = listOf(FundoInicioTopo, FundoInicioMeio, FundoInicioBase),
        icone = Icons.Filled.Info,
        titulo = resultadoTriagem,
        subtitulo = ""
    )
}

@Composable
fun TelaResultado(dados: ValidacaoResponse, onVoltar: () -> Unit) {
    val estilo = estiloPara(dados.resultadoTriagem)

    GateGradientBackground(cores = estilo.cores) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(VidroFundoForte, RoundedCornerShape(50)),
                contentAlignment = Alignment.Center
            ) {
                Icon(estilo.icone, contentDescription = null, tint = TextoBranco, modifier = Modifier.size(56.dp))
            }

            Spacer(Modifier.height(20.dp))
            Text(estilo.titulo, color = TextoBranco, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            if (estilo.subtitulo.isNotEmpty()) {
                Text(estilo.subtitulo, color = TextoBrancoForte, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp), textAlign = TextAlign.Center)
            } else {
                Spacer(Modifier.height(24.dp))
            }

            GlassCard(modifier = Modifier.fillMaxWidth(), fundo = VidroFundoForte) {
                Text("Tempo restante", color = TextoBrancoMedio, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                NumeroDestaque(valor = "${dados.tempoRestanteMinutos}", sufixo = "min")

                dados.plataformaNumero?.let { numero ->
                    Spacer(Modifier.height(16.dp))
                    Text("Plataforma", color = TextoBrancoMedio, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NumeroDestaque(valor = numero)
                        dados.statusOcupacao?.let {
                            Spacer(Modifier.width(12.dp))
                            StatusPill(texto = it)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = onVoltar,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoBranco)
            ) {
                Text("← Voltar")
            }
        }
    }
}

@Composable
fun TelaErro(mensagem: String, onVoltar: () -> Unit) {
    GateGradientBackground(cores = listOf(AlertaTopo, AlertaMeio, AlertaBase)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = TextoBranco, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(16.dp))
            Text("Não foi possível validar", color = TextoBranco, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(mensagem, color = TextoBrancoForte, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))
            OutlinedButton(onClick = onVoltar, colors = ButtonDefaults.outlinedButtonColors(contentColor = TextoBranco)) {
                Text("Tentar novamente")
            }
        }
    }
}