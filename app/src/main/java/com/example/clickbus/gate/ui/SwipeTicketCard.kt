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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.clickbus.gate.R
import com.example.clickbus.gate.modelo.Ticket
import com.example.clickbus.gate.modelo.formatarDataHora
import com.example.clickbus.gate.regras.DecisaoGate
import com.example.clickbus.gate.validacao.validarTicket
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private const val LIMIT_SWIPE = 150f

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
    ticket: Ticket,
    onValidado: (DecisaoGate) -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var mostrarQr by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
                .background(Color(0xFF639922), RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.ValidationTicket), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetX.value >= LIMIT_SWIPE) mostrarQr = true
                            scope.launch { offsetX.animateTo(0f, spring()) }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val novo = (offsetX.value + dragAmount.x).coerceIn(0f, LIMIT_SWIPE * 1.3f)
                                offsetX.snapTo(novo)
                            }
                        }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF303042))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "Passageiro", valor = ticket.passageiro)
                    InfoItem(label = "Plataforma", valor = ticket.plataforma.nome)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "Origem", valor = ticket.origem)
                    InfoItem(label = "Destino", valor = ticket.destino)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    InfoItem(label = "Embarque", valor = formatarDataHora(ticket.dataHoraEmbarque))
                    InfoItem(label = "Ocupação", valor = ticket.plataforma.ocupacao.name)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.ValidationTicket),
                    color = Color(0x66FFFFFF), fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    if (mostrarQr) {
        val qrConteudo = "GATE:${ticket.passageiro}:${ticket.plataforma.nome}:${formatarDataHora(ticket.dataHoraEmbarque)}"
        val qrBitmap = remember(qrConteudo) { gerarQrBitmap(qrConteudo) }

        Dialog(onDismissRequest = { mostrarQr = false }) {
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
                    Text(ticket.passageiro, fontSize = 13.sp, color = Color.Gray)
                    Text("${ticket.origem} → ${ticket.destino}", fontSize = 12.sp, color = Color.Gray)
                    Text(formatarDataHora(ticket.dataHoraEmbarque), fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(240.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            mostrarQr = false
                            onValidado(validarTicket(ticket))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF639922))
                    ) {
                        Text("Confirmar e Fechar")
                    }
                }
            }
        }
    }
}
