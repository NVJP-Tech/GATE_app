package com.example.clickbus.gate.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GateGradientBackground(
    cores: List<Color>,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(cores))
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopStart)
                .offset((-60).dp, 40.dp)
                .background(VidroFundoSutil, shape = RoundedCornerShape(50))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomEnd)
                .offset(60.dp, (-40).dp)
                .background(VidroFundoSutil, shape = RoundedCornerShape(50))
                .blur(80.dp)
        )
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    fundo: Color = VidroFundoMedio,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(fundo)
            .border(1.dp, VidroBorda, RoundedCornerShape(28.dp))
            .padding(24.dp),
        content = content
    )
}

@Composable
fun StatusPill(texto: String, cor: Color = VidroFundoForte) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(cor)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = texto, color = TextoBranco, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MolduraDeScanner(modifier: Modifier = Modifier, tamanho: Dp = 200.dp) {
    Box(
        modifier = modifier
            .size(tamanho)
            .background(VidroFundoSutil, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val comprimento = 32.dp.toPx()
            val espessura = 4.dp.toPx()
            val cor = Color.White
            val w = size.width
            val h = size.height

            drawLine(cor, Offset(0f, 0f), Offset(comprimento, 0f), espessura)
            drawLine(cor, Offset(0f, 0f), Offset(0f, comprimento), espessura)
            drawLine(cor, Offset(w, 0f), Offset(w - comprimento, 0f), espessura)
            drawLine(cor, Offset(w, 0f), Offset(w, comprimento), espessura)
            drawLine(cor, Offset(0f, h), Offset(comprimento, h), espessura)
            drawLine(cor, Offset(0f, h), Offset(0f, h - comprimento), espessura)
            drawLine(cor, Offset(w, h), Offset(w - comprimento, h), espessura)
            drawLine(cor, Offset(w, h), Offset(w, h - comprimento), espessura)
        }
    }
}

@Composable
fun NumeroDestaque(valor: String, sufixo: String? = null) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(text = valor, color = TextoBranco, fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 56.sp)
        sufixo?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = it, color = TextoBrancoMedio, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}