package com.example.opportunitymanagement.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SesionDao {
    /**
     * Usuario de la sesión activa, o null si no hay sesión o la cuenta fue desactivada.
     * Al ser un Flow, Room vuelve a emitir cuando cambian la sesión o el usuario.
     */
    @Query(
        """
        SELECT u.* FROM usuario u
        INNER JOIN sesion s ON s.userId = u.id
        WHERE u.isActive = 1
        LIMIT 1
        """
    )
    fun usuarioActual(): Flow<UsuarioEntity?>

    @Upsert
    suspend fun guardar(sesion: SesionEntity)

    @Query("DELETE FROM sesion")
    suspend fun cerrar()
}
