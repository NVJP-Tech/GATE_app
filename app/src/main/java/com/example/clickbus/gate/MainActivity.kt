package com.example.clickbus.gate

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clickbus.gate.api.dto.ValidacaoResponse
import com.example.clickbus.gate.ui.PendenciasScreen
import com.example.clickbus.gate.ui.SwipeTicketCard
import com.example.clickbus.gate.ui.TelaErro
import com.example.clickbus.gate.ui.TelaResultado
import com.example.clickbus.gate.ui.theme.*
import com.example.clickbus.gate.validacao.validarTicket
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GateClickBusTheme { GateApp() }
        }
    }
}

private val CODIGOS_DE_TESTE = listOf(
    "QR-NO-HORARIO-001",
    "QR-ADIANTADO-002",
    "QR-ATRASADO-003"
)

private sealed class TelaEstado {
    data object Formulario : TelaEstado()
    data object Carregando : TelaEstado()
    data class Resultado(val dados: ValidacaoResponse) : TelaEstado()
    data class Erro(val mensagem: String) : TelaEstado()
    data object Pendencias : TelaEstado()
}

@Composable
fun GateApp() {
    var estado by remember { mutableStateOf<TelaEstado>(TelaEstado.Formulario) }
    var codigoQr by remember { mutableStateOf(CODIGOS_DE_TESTE.first()) }
    var canalAppGps by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val permissaoLocalizacao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    fun executarValidacao() {
        estado = TelaEstado.Carregando
        coroutineScope.launch {
            try {
                val response = validarTicket(
                    context = context,
                    codigoQr = codigoQr,
                    canalAppGps = canalAppGps,
                    solicitarPermissaoLocalizacao = {
                        permissaoLocalizacao.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                )
                estado = TelaEstado.Resultado(response)
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                estado = TelaEstado.Erro(e.localizedMessage ?: "Erro na comunicação com a API")
            }
        }
    }

    when (val atual = estado) {
        is TelaEstado.Formulario -> TelaFormulario(
            codigoQr = codigoQr,
            onCodigoQrChange = { codigoQr = it },
            canalAppGps = canalAppGps,
            onCanalChange = { canalAppGps = it },
            onSolicitarPermissaoLocalizacao = { permissaoLocalizacao.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
            onCarregando = { executarValidacao() },
            onResultado = { estado = TelaEstado.Resultado(it) },
            onErro = { estado = TelaEstado.Erro(it) },
            onAbrirPendencias = { estado = TelaEstado.Pendencias }
        )
        is TelaEstado.Carregando -> TelaCarregando()
        is TelaEstado.Resultado -> TelaResultado(atual.dados, onVoltar = { estado = TelaEstado.Formulario })
        is TelaEstado.Erro -> TelaErro(atual.mensagem, onVoltar = { estado = TelaEstado.Formulario })
        is TelaEstado.Pendencias -> PendenciasScreen(onVoltar = { estado = TelaEstado.Formulario })
    }
}

@Composable
private fun TelaFormulario(
    codigoQr: String,
    onCodigoQrChange: (String) -> Unit,
    canalAppGps: Boolean,
    onCanalChange: (Boolean) -> Unit,
    onSolicitarPermissaoLocalizacao: () -> Unit,
    onCarregando: () -> Unit,
    onResultado: (ValidacaoResponse) -> Unit,
    onErro: (String) -> Unit,
    onAbrirPendencias: () -> Unit
) {
    GateGradientBackground(cores = listOf(FundoInicioTopo, FundoInicioMeio, FundoInicioBase)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onAbrirPendencias) {
                    Text("Pendências", color = TextoBrancoMedio, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(8.dp))
            Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = TextoBranco, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text("GATE ClickBus", color = TextoBranco, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Aproxime o QR Code para validar", color = TextoBrancoFraco, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            MolduraDeScanner()

            Spacer(Modifier.height(20.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Código do ticket (teste)", color = TextoBrancoMedio, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                CODIGOS_DE_TESTE.forEach { codigo ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = codigoQr == codigo,
                            onClick = { onCodigoQrChange(codigo) },
                            colors = RadioButtonDefaults.colors(selectedColor = TextoBranco, unselectedColor = TextoBrancoFraco)
                        )
                        Text(codigo, color = TextoBranco, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (canalAppGps) "Canal: App (GPS)" else "Canal: Totem GATE (QR)",
                        color = TextoBranco, fontSize = 13.sp, modifier = Modifier.weight(1f)
                    )
                    Switch(checked = canalAppGps, onCheckedChange = onCanalChange)
                }
            }

            Spacer(Modifier.height(20.dp))

            SwipeTicketCard(
                codigoQr = codigoQr,
                canalAppGps = canalAppGps,
                onSolicitarPermissaoLocalizacao = onSolicitarPermissaoLocalizacao,
                onCarregando = onCarregando,
                onResultado = onResultado,
                onErro = onErro
            )
        }
    }
}

@Composable
private fun TelaCarregando() {
    GateGradientBackground(cores = listOf(FundoInicioTopo, FundoInicioMeio, FundoInicioBase)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(color = TextoBranco, strokeWidth = 4.dp, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(24.dp))
            Text("Validando passagem...", color = TextoBranco, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Aguarde enquanto processamos", color = TextoBrancoFraco, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}