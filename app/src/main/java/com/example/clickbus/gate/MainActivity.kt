package com.example.clickbus.gate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        enableEdgeToEdge()
        setContent {
            GateClickBusTheme {
                    GateTela()
                }
            }
        }
    }

@Composable
fun GateTela() {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF1a1a2e)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GATE - ClickBus",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 48.dp, bottom = 8.dp)
            )
        Text(
            text = "Simulador MVP",
            color = Color(0x99FFFFFF),
            fontSize = 14.sp
        )
    }
}


