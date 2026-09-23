package com.example.clickbus.gate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clickbus.gate.api.RetrofitClient
import com.example.clickbus.gate.api.dto.TicketDto
import com.example.clickbus.gate.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinhasViagensScreen(
    onSelectTicket: (String) -> Unit,
    onVoltar: () -> Unit
) {
    var tickets by remember { mutableStateOf<List<TicketDto>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            // Utiliza diretamente o RetrofitClient já configurado no projeto
            tickets = RetrofitClient.api.listarTickets()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            carregando = false
        }
    }

    GateGradientBackground(cores = listOf(FundoInicioTopo, FundoInicioMeio, FundoInicioBase)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Minhas Viagens", color = TextoBranco, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onVoltar) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextoBranco)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            if (carregando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TextoBranco)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tickets) { ticket ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectTicket(ticket.codigoQr) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = TextoBranco,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${ticket.viagem?.origem ?: "N/A"} ➔ ${ticket.viagem?.destino ?: "N/A"}",
                                        color = TextoBranco,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Poltrona ${ticket.poltrona}",
                                        color = TextoBrancoMedio,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Código: ${ticket.codigoQr}",
                                        color = TextoBrancoFraco,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}