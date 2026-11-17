package com.example.opportunitymanagement.data.repository

import com.example.opportunitymanagement.data.local.SesionDao
import com.example.opportunitymanagement.data.local.SesionEntity
import com.example.opportunitymanagement.data.local.UsuarioEntity
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val dao: SesionDao) {
    val usuarioActual: Flow<UsuarioEntity?> = dao.usuarioActual()

    suspend fun iniciar(userId: Long) {
        require(userId > 0) { "El usuario de la sesión no es válido" }
        dao.guardar(SesionEntity(userId = userId, iniciadaEn = System.currentTimeMillis()))
    }

    suspend fun cerrar() = dao.cerrar()
}
