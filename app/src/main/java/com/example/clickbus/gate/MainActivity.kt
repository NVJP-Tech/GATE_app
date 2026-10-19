package com.example.clickbus.gate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.clickbus.gate.api.dto.ValidacaoResponse
import com.example.clickbus.gate.api.mensagemDeErro
import com.example.clickbus.gate.deslocamento.MonitorDeslocamento
import com.example.clickbus.gate.notificacao.NotificacaoAgendador
import com.example.clickbus.gate.notificacao.NotificacaoHelper
import com.example.clickbus.gate.ui.MinhasViagensScreen
import com.example.clickbus.gate.ui.PendenciasScreen
import com.example.clickbus.gate.ui.SwipeTicketCard
import com.example.clickbus.gate.ui.TelaErro
import com.example.clickbus.gate.ui.TelaResultado
import com.example.clickbus.gate.ui.mapa.MapaPlataformaScreen
import com.example.clickbus.gate.ui.theme.FundoInicioBase
import com.example.clickbus.gate.ui.theme.FundoInicioMeio
import com.example.clickbus.gate.ui.theme.FundoInicioTopo
import com.example.clickbus.gate.ui.theme.GateClickBusTheme
import com.example.clickbus.gate.ui.theme.GateGradientBackground
import com.example.clickbus.gate.ui.theme.GlassCard
import com.example.clickbus.gate.ui.theme.MolduraDeScanner
import com.example.clickbus.gate.ui.theme.TextoBranco
import com.example.clickbus.gate.ui.theme.TextoBrancoFraco
import com.example.clickbus.gate.ui.theme.TextoBrancoMedio
import com.example.clickbus.gate.validacao.validarTicket
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificacaoHelper.criarCanal(this)
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
    data class Mapa(val plataformaNumero: String) : TelaEstado()
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

    // Ao conceder a permissao de localizacao, comeca a monitorar o deslocamento
    val permissaoLocalizacao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) codigoQr?.let { MonitorDeslocamento.iniciar(context, it) }
    }

    val permissaoNotificacao = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !NotificacaoHelper.temPermissao(context)
        ) {
            permissaoNotificacao.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

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

                // Passageiro chegou e validou: nao precisa mais monitorar o deslocamento
                MonitorDeslocamento.parar()

                NotificacaoAgendador.agendar(
                    context = context,
                    codigoQr = codigo,
                    tempoRestanteMinutos = resultado.tempoRestanteMinutos,
                    plataformaNome = resultado.plataformaNumero
                )

                estado = if (resultado.resultadoTriagem == "EMBARQUE_IMEDIATO" && resultado.plataformaNumero != null) {
                    TelaEstado.Mapa(resultado.plataformaNumero)
                } else {
                    TelaEstado.Resultado(resultado)
                }
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
                // Ticket selecionado: comeca a monitorar o deslocamento ate o embarque
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    MonitorDeslocamento.iniciar(context, codigo)
                } else {
                    permissaoLocalizacao.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
                estado = TelaEstado.Formulario
            },
            onVoltar = { estado = TelaEstado.Formulario }
        )
        is TelaEstado.Carregando -> TelaCarregando()
        is TelaEstado.Resultado -> TelaResultado(atual.dados, onVoltar = { estado = TelaEstado.Formulario })
        is TelaEstado.Mapa -> MapaPlataformaScreen(atual.plataformaNumero, onVoltar = { estado = TelaEstado.Formulario })
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