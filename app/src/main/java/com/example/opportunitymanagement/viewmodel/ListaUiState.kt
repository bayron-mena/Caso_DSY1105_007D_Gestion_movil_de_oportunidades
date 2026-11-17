package com.example.opportunitymanagement.viewmodel

import com.example.opportunitymanagement.data.local.OportunidadConResponsable

data class ListaUiState(
    val items: List<OportunidadConResponsable> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null,
    val eliminandoId: Long? = null,
    val mensaje: String? = null
)
