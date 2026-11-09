package com.example.devmanagerweb.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UsuarioEntity::class,
        ClienteEntity::class,
        ProjetoEntity::class,
        OrcamentoEntity::class,
        FaturaEntity::class,
        HistoricoComunicacaoEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class DevManagerDatabase : RoomDatabase() {
    abstract fun devManagerDao(): DevManagerDao
}
