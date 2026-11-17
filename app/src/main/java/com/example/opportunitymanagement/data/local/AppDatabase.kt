package com.example.opportunitymanagement.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [OportunidadEntity::class,
                      UsuarioEntity::class,
                      CredentialEntity::class],
    version = 4,
    exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oportunidades(): OportunidadDao
    abstract fun usuarios(): UsuarioDao
    abstract fun credenciales(): CredentialDao

    companion object {
        /**
         * v4: el responsable deja de ser un texto libre y pasa a ser el usuario (clave foránea).
         * SQLite no permite agregar una clave foránea con ALTER TABLE, así que se crea la tabla
         * nueva, se copian los datos y se reemplaza la anterior. Los registros existentes quedan
         * sin responsable (NULL); se asigna al usuario que los vuelva a guardar.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `oportunidades_nueva` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`nombre` TEXT NOT NULL, `tipo` TEXT NOT NULL, `descripcion` TEXT NOT NULL, " +
                        "`fuente` TEXT NOT NULL, `prioridad` TEXT NOT NULL, `potencial` TEXT NOT NULL, " +
                        "`estado` TEXT NOT NULL, `observaciones` TEXT NOT NULL, `responsableId` INTEGER, " +
                        "`fechaDeteccion` INTEGER NOT NULL, `ultimaActualizacion` INTEGER NOT NULL, " +
                        "`referenciaGeografica` TEXT NOT NULL, " +
                        "FOREIGN KEY(`responsableId`) REFERENCES `usuario`(`id`) " +
                        "ON UPDATE NO ACTION ON DELETE RESTRICT )"
                )
                db.execSQL(
                    "INSERT INTO `oportunidades_nueva` (`id`, `nombre`, `tipo`, `descripcion`, " +
                        "`fuente`, `prioridad`, `potencial`, `estado`, `observaciones`, `responsableId`, " +
                        "`fechaDeteccion`, `ultimaActualizacion`, `referenciaGeografica`) " +
                        "SELECT `id`, `nombre`, `tipo`, `descripcion`, `fuente`, `prioridad`, " +
                        "`potencial`, `estado`, `observaciones`, NULL, `fechaDeteccion`, " +
                        "`ultimaActualizacion`, `referenciaGeografica` FROM `oportunidades`"
                )
                db.execSQL("DROP TABLE `oportunidades`")
                db.execSQL("ALTER TABLE `oportunidades_nueva` RENAME TO `oportunidades`")
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_oportunidades_responsableId` " +
                        "ON `oportunidades` (`responsableId`)"
                )
            }
        }

        @Volatile
        private var instancia: AppDatabase? = null
        fun obtener(context: Context): AppDatabase = instancia ?: synchronized(this) {
            instancia ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "oportunidades.db"
            ).addMigrations(MIGRATION_3_4)
                .build().also { instancia = it }
        }
    }
}
