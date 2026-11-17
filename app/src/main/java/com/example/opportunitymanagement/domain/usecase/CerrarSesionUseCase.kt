package com.example.opportunitymanagement.domain.usecase

import com.example.opportunitymanagement.data.repository.SessionRepository

class CerrarSesionUseCase(private val sessionRepository: SessionRepository) {
    suspend operator fun invoke() = sessionRepository.cerrar()
}
