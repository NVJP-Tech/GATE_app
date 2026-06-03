package com.example.clickbus.gate.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun InfoItem(label: String, valor: String) {
    Column {
        Text(
            text = label,
            color = Color(0x99FFFFFF),
            fontSize = 11.sp
        )
        Text(
            text = valor,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
