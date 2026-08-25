package com.example.clickbus.gate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clickbus.gate.api.RetrofitClient
import com.example.clickbus.gate.api.dto.RemarcacaoResponse
import com.example.clickbus.gate.api.mensagemDeErro
import com.example.clickbus.gate.ui.theme.FundoInicioTopo
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Composable
fun PendenciasScreen(onVoltar: () -> Unit) {
    var pendentes by remember { mutableStateOf<List<RemarcacaoResponse>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                pendentes = RetrofitClient.api.listarRemarcacoesPendentes()
            } catch (e: HttpException) {
                erro = e.mensagemDeErro()
            } catch (e: Exception) {
                erro = "Não foi possível conectar à API."
            } finally {
                carregando = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF1a1a2e))) {
        Row(
            modifier = Modifier.fillMaxWidth().background(FundoInicioTopo)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onVoltar) {
                Text("← Voltar", color = Color(0xFF4F9CF9), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Remarcações Pendentes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        when {
            carregando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4F9CF9))
            }
            erro != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(erro!!, color = Color(0xFFE24B4A), fontSize = 13.sp, modifier = Modifier.padding(24.dp))
            }
            pendentes.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhuma remarcação pendente.", color = Color(0x99FFFFFF), fontSize = 14.sp)
            }
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pendentes) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF303042))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.codigoQrTicketOriginal, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFE24B4A)) {
                                    Text(item.status, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                }
                            }
                            item.motivo?.let {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(it, color = Color(0xCCFFFFFF), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Registrado em: ${item.dataHora.take(16).replace("T", " ")}",
                                color = Color(0x99FFFFFF), fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}