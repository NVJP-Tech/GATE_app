package com.example.clickbus.gate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clickbus.gate.modelo.formatarHoraAtual
import com.example.clickbus.gate.modelo.gerarTicketAleatorio
import com.example.clickbus.gate.modelo.usuarioLogado
import com.example.clickbus.gate.regras.DecisaoGate
import com.example.clickbus.gate.ui.HistoricoScreen
import com.example.clickbus.gate.ui.ResultadoCard
import com.example.clickbus.gate.ui.SwipeTicketCard
import com.example.clickbus.gate.ui.theme.GateClickBusTheme
import kotlinx.coroutines.delay
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GateClickBusTheme { AppRoot() } }
    }
}

@Composable
fun AppRoot() {
    var telaHistorico by remember { mutableStateOf(false) }
    if (telaHistorico) {
        HistoricoScreen(usuarioId = usuarioLogado.id, onVoltar = { telaHistorico = false })
    } else {
        GateTela(onAbrirHistorico = { telaHistorico = true })
    }
}

@Composable
fun GateTela(onAbrirHistorico: () -> Unit) {
    var ticket by remember { mutableStateOf(gerarTicketAleatorio(usuarioLogado)) }
    var decisao by remember { mutableStateOf<DecisaoGate?>(null) }
    var agora by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) { delay(1000); agora = LocalDateTime.now() }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1a1a2e)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF16213E))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatarHoraAtual(agora), color = Color(0xFF4F9CF9), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            TextButton(onClick = onAbrirHistorico) {
                Text("Histórico", color = Color(0xFF4F9CF9), fontSize = 13.sp)
            }
        }

        Text("GATE ClickBus", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp, bottom = 2.dp))
        Text(usuarioLogado.nome, color = Color(0x99FFFFFF), fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 24.dp))

        SwipeTicketCard(ticket = ticket, onValidado = { decisao = it })

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { ticket = gerarTicketAleatorio(usuarioLogado); decisao = null },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F9CF9))
        ) {
            Text("Gerar Novo Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
        decisao?.let { ResultadoCard(it) }
    }
}
