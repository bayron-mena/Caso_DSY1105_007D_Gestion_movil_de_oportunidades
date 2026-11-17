package com.example.opportunitymanagement.domain.usecase

import com.example.opportunitymanagement.data.local.UsuarioEntity
import com.example.opportunitymanagement.data.repository.AuthRepository
import com.example.opportunitymanagement.data.repository.SessionRepository

/**
 * Autentica al usuario y, si las credenciales son válidas, deja la sesión guardada
 * para que el próximo inicio de la app vaya directo al menú.
 */
class LoginUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(email: String, password: String): UsuarioEntity? {
        val user = authRepository.login(email, password) ?: return null
        sessionRepository.iniciar(user.id)
        return user
    }
}
