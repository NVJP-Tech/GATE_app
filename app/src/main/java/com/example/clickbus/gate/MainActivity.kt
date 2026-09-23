package com.example.clickbus.gate

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
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
import com.example.clickbus.gate.api.mensagemDeErro
import com.example.clickbus.gate.ui.MinhasViagensScreen
import com.example.clickbus.gate.ui.PendenciasScreen
import com.example.clickbus.gate.ui.SwipeTicketCard
import com.example.clickbus.gate.ui.TelaErro
import com.example.clickbus.gate.ui.TelaResultado
import com.example.clickbus.gate.ui.theme.*
import com.example.clickbus.gate.validacao.validarTicket
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GateClickBusTheme { GateApp() }
        }
    }
}

private sealed class TelaEstado {
    data object Formulario : TelaEstado()
    data object MinhasViagens : TelaEstado()
    data object Carregando : TelaEstado()
    data class Resultado(val dados: ValidacaoResponse) : TelaEstado()
    data class Erro(val mensagem: String) : TelaEstado()
    data object Pendencias : TelaEstado()
}

@Composable
fun GateApp() {
    val context = LocalContext.current
    // Este escopo pertence ao GateApp, que NUNCA sai de composicao -
    // por isso e seguro lancar a chamada de rede aqui, mesmo trocando
    // de tela logo em seguida (diferente de lancar dentro de um
    // composable que sera removido, como SwipeTicketCard).
    val escopo = rememberCoroutineScope()

    var estado by remember { mutableStateOf<TelaEstado>(TelaEstado.Formulario) }
    var codigoQr by remember { mutableStateOf<String?>(null) }
    var canalAppGps by remember { mutableStateOf(false) }

    val permissaoLocalizacao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    fun executarValidacao(codigo: String) {
        estado = TelaEstado.Carregando
        escopo.launch {
            try {
                val resultado = validarTicket(
                    context = context,
                    codigoQr = codigo,
                    canalAppGps = canalAppGps,
                    solicitarPermissaoLocalizacao = { permissaoLocalizacao.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                )
                estado = TelaEstado.Resultado(resultado)
            } catch (e: HttpException) {
                estado = TelaEstado.Erro(e.mensagemDeErro())
            } catch (e: Exception) {
                estado = TelaEstado.Erro("Não foi possível conectar à API. Verifique se ela está no ar.")
            }
        }
    }

    when (val atual = estado) {
        is TelaEstado.Formulario -> TelaFormulario(
            codigoQr = codigoQr,
            canalAppGps = canalAppGps,
            onCanalChange = { canalAppGps = it },
            onAbrirMinhasViagens = { estado = TelaEstado.MinhasViagens },
            onConfirmarValidacao = { codigo -> executarValidacao(codigo) },
            onAbrirPendencias = { estado = TelaEstado.Pendencias }
        )
        is TelaEstado.MinhasViagens -> MinhasViagensScreen(
            onSelectTicket = { codigo ->
                codigoQr = codigo
                estado = TelaEstado.Formulario
            },
            onVoltar = { estado = TelaEstado.Formulario }
        )
        is TelaEstado.Carregando -> TelaCarregando()
        is TelaEstado.Resultado -> TelaResultado(atual.dados, onVoltar = { estado = TelaEstado.Formulario })
        is TelaEstado.Erro -> TelaErro(atual.mensagem, onVoltar = { estado = TelaEstado.Formulario })
        is TelaEstado.Pendencias -> PendenciasScreen(onVoltar = { estado = TelaEstado.Formulario })
    }
}

@Composable
private fun TelaFormulario(
    codigoQr: String?,
    canalAppGps: Boolean,
    onCanalChange: (Boolean) -> Unit,
    onAbrirMinhasViagens: () -> Unit,
    onConfirmarValidacao: (String) -> Unit,
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
            Text("Selecione um ticket para validar", color = TextoBrancoFraco, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            MolduraDeScanner()

            Spacer(Modifier.height(20.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onAbrirMinhasViagens)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, tint = TextoBranco, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ticket selecionado", color = TextoBrancoMedio, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = codigoQr ?: "Toque para escolher em Minhas Viagens",
                            color = TextoBranco,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (canalAppGps) "Canal: App (GPS)" else "Canal: Totem GATE (QR)",
                        color = TextoBranco, fontSize = 13.sp, modifier = Modifier.weight(1f)
                    )
                    Switch(checked = canalAppGps, onCheckedChange = onCanalChange)
                }
            }

            Spacer(Modifier.height(20.dp))

            if (codigoQr != null) {
                SwipeTicketCard(
                    codigoQr = codigoQr,
                    canalAppGps = canalAppGps,
                    onConfirmarValidacao = { onConfirmarValidacao(codigoQr) }
                )
            }
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