package com.example.opportunitymanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.opportunitymanagement.di.appContainer
import com.example.opportunitymanagement.domain.usecase.CerrarSesionUseCase
import com.example.opportunitymanagement.domain.usecase.ObservarSesionUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Fuente única de verdad sobre quién está autenticado. Decide la pantalla inicial
 * y hace que la navegación reaccione al iniciar o cerrar sesión.
 */
class SessionViewModel(
    observarSesion: ObservarSesionUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase
) : ViewModel() {

    val uiState: StateFlow<SessionUiState> = observarSesion()
        .map { SessionUiState(verificando = false, usuario = it) }
        // Si la sesión no se puede leer, se pide login en lugar de dejar la app bloqueada.
        .catch { emit(SessionUiState(verificando = false, usuario = null)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionUiState())

    fun cerrarSesion() {
        viewModelScope.launch { cerrarSesionUseCase() }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                SessionViewModel(container.observarSesionUseCase, container.cerrarSesionUseCase)
            }
        }
    }
}
