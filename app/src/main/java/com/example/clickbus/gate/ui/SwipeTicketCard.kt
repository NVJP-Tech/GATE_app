package com.example.clickbus.gate.ui

import android.graphics.Bitmap
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clickbus.gate.api.dto.ValidacaoResponse
import com.example.clickbus.gate.api.mensagemDeErro
import com.example.clickbus.gate.ui.theme.TextoBranco
import com.example.clickbus.gate.validacao.validarTicket
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.math.roundToInt

private const val LIMITE_SWIPE = 150f

private fun gerarQrBitmap(conteudo: String, tamanho: Int = 512): Bitmap {
    val matrix = QRCodeWriter().encode(conteudo, BarcodeFormat.QR_CODE, tamanho, tamanho)
    val bitmap = Bitmap.createBitmap(tamanho, tamanho, Bitmap.Config.RGB_565)
    for (x in 0 until tamanho) {
        for (y in 0 until tamanho) {
            bitmap.setPixel(x, y, if (matrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
        }
    }
    return bitmap
}

@Composable
fun SwipeTicketCard(
    codigoQr: String,
    canalAppGps: Boolean,
    onSolicitarPermissaoLocalizacao: () -> Unit,
    onCarregando: () -> Unit,
    onResultado: (ValidacaoResponse) -> Unit,
    onErro: (String) -> Unit
) {
    val context = LocalContext.current
    val offsetX = remember { Animatable(0f) }

    // 1. Escopo para animações do Card (pode morrer ao desmontar)
    val cardScope = rememberCoroutineScope()

    // 2. Escopo para requisição HTTP (mantido via composition para durar a transição)
    val apiScope = rememberCoroutineScope()

    var mostrarQr by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .background(Color(0xFF16A34A), RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("→  Solte para revelar o QR", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(codigoQr) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetX.value >= LIMITE_SWIPE) mostrarQr = true
                            cardScope.launch { offsetX.animateTo(0f, spring()) }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            cardScope.launch {
                                val novo = (offsetX.value + dragAmount.x).coerceIn(0f, LIMITE_SWIPE * 1.3f)
                                offsetX.snapTo(novo)
                            }
                        }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF303042))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                InfoItem(label = "Código do ticket", valor = codigoQr)
                Spacer(modifier = Modifier.height(12.dp))
                InfoItem(label = "Canal", valor = if (canalAppGps) "App (GPS)" else "Totem GATE (QR)")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "→  Arraste para revelar o QR e validar",
                    color = Color(0x66FFFFFF), fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (mostrarQr) {
        var validando by remember { mutableStateOf(false) }
        val qrBitmap = remember(codigoQr) { gerarQrBitmap(codigoQr) }

        Dialog(onDismissRequest = { if (!validando) mostrarQr = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("QR de Validação", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(codigoQr, fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(220.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            mostrarQr = false
                            // Dispara o fluxo da MainActivity (que muda pra TelaCarregando e faz a chamada HTTP)
                            onCarregando()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                    ) {
                        Text("Confirmar e Validar")
                    }
                }
            }
        }
    }
}