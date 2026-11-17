package com.example.opportunitymanagement.domain.usecase

import com.example.opportunitymanagement.data.local.UsuarioEntity
import com.example.opportunitymanagement.data.repository.SessionRepository
import kotlinx.coroutines.flow.Flow

/** Usuario autenticado en el dispositivo; emite null cuando no hay sesión válida. */
class ObservarSesionUseCase(private val sessionRepository: SessionRepository) {
    operator fun invoke(): Flow<UsuarioEntity?> = sessionRepository.usuarioActual
}
