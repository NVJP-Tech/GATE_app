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
import com.example.clickbus.gate.ui.theme.GateClickBusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GateClickBusTheme {
                GateTela()
            }
        }
    }
}

@Composable
fun GateTela() {
    var ticket by remember { mutableStateOf(gerarTicketAleatorio()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1a1a2e)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GATE ClickBus",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 48.dp, bottom = 4.dp)
        )
        Text(
            text = "Simulador MVP",
            color = Color(0x99FFFFFF),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF303042))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(label = "Passageiro", valor = ticket.passageiro)
                    InfoItem(label = "Plataforma", valor = ticket.plataforma.nome)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(label = "Origem", valor = ticket.origem)
                    InfoItem(label = "Destino", valor = ticket.destino)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem(
                        label = "Embarque",
                        valor = minutosParaHorario(ticket.horarioEmbarqueMinutos)
                    )
                    InfoItem(
                        label = "Ocupação",
                        valor = ticket.plataforma.ocupacao.name
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { ticket = gerarTicketAleatorio() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F9CF9))
        ) {
            Text(
                text = "Gerar Novo Ticket",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InfoItem(label: String, valor: String) {
    Column {
        Text(
            text = label,
            color = Color(0x99FFFFFF),
            fontSize = 11.sp
        )
        Text(
            text = valor,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}