package com.example.opportunitymanagement.viewmodel

import com.example.opportunitymanagement.data.local.UsuarioEntity

data class SessionUiState(
    val verificando: Boolean = true,
    val usuario: UsuarioEntity? = null
)
