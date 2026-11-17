package com.example.opportunitymanagement.data.local

import androidx.room.Embedded

/**
 * Resultado de la consulta que une una oportunidad con el nombre de su responsable.
 * No es una tabla: Room la llena a partir del JOIN en OportunidadDao.
 */
data class OportunidadConResponsable(
    @Embedded val oportunidad: OportunidadEntity,
    // Null cuando la oportunidad no tiene responsable asignado.
    val responsableNombre: String?
)
