package com.example.opportunitymanagement.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.opportunitymanagement.ui.splash.SplashScreen
import com.example.opportunitymanagement.viewmodel.SessionViewModel

/**
 * Raíz de la UI. Mientras se lee la sesión guardada muestra el splash; luego arma la
 * navegación partiendo del menú (sesión válida) o del login (sin sesión).
 */
@Composable
fun AppRoot(sessionViewModel: SessionViewModel = viewModel(factory = SessionViewModel.Factory)) {
    val session by sessionViewModel.uiState.collectAsStateWithLifecycle()
    if (session.verificando) {
        SplashScreen()
    } else {
        AppNavigation(session = session, onLogout = sessionViewModel::cerrarSesion)
    }
}
