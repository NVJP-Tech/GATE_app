package com.example.clickbus.gate.ui.mapa

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.dp

@Composable
fun MapaPlataformaScreen(plataformaNome: String, onVoltar: () -> Unit) {
    val destino = RotaEstatica.normalizar(plataformaNome)
    val caminho = RotaEstatica.caminho(destino)
    val measurer = rememberTextMeasurer()
    val verde = Color(0xFF639922)
    val cinza = Color(0xFF9E9E9E)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Siga até a Plataforma $destino", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        Canvas(Modifier.fillMaxWidth().aspectRatio(0.9f)) {
            fun px(p: RotaEstatica.Ponto) = Offset(p.x * size.width, p.y * size.height)

            RotaEstatica.PLATAFORMAS.forEach { (nome, p) ->
                val ativa = nome == destino
                val o = px(p)
                drawRoundRect(
                    color = if (ativa) verde else cinza,
                    topLeft = Offset(o.x - 32.dp.toPx(), o.y - 24.dp.toPx()),
                    size = Size(64.dp.toPx(), 48.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
                drawText(
                    measurer, nome, Offset(o.x - 6.dp.toPx(), o.y - 10.dp.toPx()),
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold)
                )
            }

            for (i in 0 until caminho.size - 1) {
                drawLine(verde, px(caminho[i]), px(caminho[i + 1]),
                    strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
            }

            drawCircle(Color(0xFF378ADD), 10.dp.toPx(), px(RotaEstatica.ENTRADA))
            drawText(
                measurer, "Entrada",
                Offset(px(RotaEstatica.ENTRADA).x - 26.dp.toPx(), px(RotaEstatica.ENTRADA).y + 12.dp.toPx())
            )
        }

        Spacer(Modifier.height(16.dp))
        RotaEstatica.passos(destino).forEachIndexed { i, texto ->
            Text("${i + 1}. $texto", Modifier.padding(vertical = 4.dp))
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = onVoltar, Modifier.fillMaxWidth()) { Text("Voltar") }
    }
}