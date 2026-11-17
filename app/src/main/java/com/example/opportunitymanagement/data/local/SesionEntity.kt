package com.example.opportunitymanagement.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Sesión local del dispositivo. La tabla tiene como máximo una fila (id = [ID_UNICO]):
 * si existe, hay un usuario autenticado. Solo se guarda el id, nunca credenciales.
 */
@Entity(
    tableName = "sesion",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class SesionEntity(
    @PrimaryKey val id: Int = ID_UNICO,
    val userId: Long,
    val iniciadaEn: Long
) {
    companion object {
        const val ID_UNICO = 1
    }
}
