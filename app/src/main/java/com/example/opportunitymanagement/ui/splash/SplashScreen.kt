package com.example.opportunitymanagement.ui.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.opportunitymanagement.ui.theme.Caso_DSY1105_007D_Gestion_movil_de_oportunidadesTheme

/** Se muestra mientras se verifica si hay una sesión guardada. */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Gestión de oportunidades", style = MaterialTheme.typography.headlineMedium)
        CircularProgressIndicator()
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashPreview() {
    Caso_DSY1105_007D_Gestion_movil_de_oportunidadesTheme { SplashScreen() }
}
