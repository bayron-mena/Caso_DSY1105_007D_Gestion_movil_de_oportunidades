package com.example.opportunitymanagement
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.opportunitymanagement.ui.navigation.AppRoot
import com.example.opportunitymanagement.ui.theme.Caso_DSY1105_007D_Gestion_movil_de_oportunidadesTheme

/** Solo hospeda la UI: las dependencias viven en [OpportunityApplication.container]. */
class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent {
   Caso_DSY1105_007D_Gestion_movil_de_oportunidadesTheme {
    Surface(Modifier.fillMaxSize()) { AppRoot() }
   }
  }
 }
}
