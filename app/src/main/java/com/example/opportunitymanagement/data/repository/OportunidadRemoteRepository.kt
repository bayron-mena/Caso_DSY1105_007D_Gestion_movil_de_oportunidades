package com.example.opportunitymanagement.data.repository

import com.example.opportunitymanagement.data.local.OportunidadEntity
import com.example.opportunitymanagement.data.remote.OportunidadApi
import com.example.opportunitymanagement.data.remote.toDto
import com.example.opportunitymanagement.data.remote.toEntity
import retrofit2.HttpException

/**
 * Acceso a las oportunidades mediante la API REST.
 * Los fallos de red llegan como IOException y los códigos de error como HttpException.
 */
class OportunidadRemoteRepository(private val api: OportunidadApi) {

    suspend fun listar(): List<OportunidadEntity> = api.listar().map { it.toEntity() }

    suspend fun buscar(id: Long): OportunidadEntity = api.buscar(id).toEntity()

    suspend fun crear(item: OportunidadEntity): OportunidadEntity =
        api.crear(item.toDto().copy(id = null)).toEntity()

    suspend fun actualizar(item: OportunidadEntity): OportunidadEntity =
        api.actualizar(item.id, item.toDto()).toEntity()

    suspend fun eliminar(id: Long) {
        val respuesta = api.eliminar(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }
}
