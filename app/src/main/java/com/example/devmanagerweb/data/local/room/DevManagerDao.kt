package com.example.devmanagerweb.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DevManagerDao {
    @Query("SELECT * FROM usuarios")
    fun getUsuarios(): Flow<List<UsuarioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM clientes")
    fun getClientes(): Flow<List<ClienteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity): Long

    @Query("SELECT * FROM projetos")
    fun getProjetos(): Flow<List<ProjetoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjeto(projeto: ProjetoEntity): Long

    @Query("SELECT * FROM orcamentos")
    fun getOrcamentos(): Flow<List<OrcamentoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrcamento(orcamento: OrcamentoEntity): Long

    @Query("SELECT * FROM faturas")
    fun getFaturas(): Flow<List<FaturaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFatura(fatura: FaturaEntity): Long

    @Query("DELETE FROM usuarios")
    suspend fun clearUsuarios()

    @Query("DELETE FROM clientes")
    suspend fun clearClientes()

    @Query("DELETE FROM projetos")
    suspend fun clearProjetos()

    @Query("DELETE FROM orcamentos")
    suspend fun clearOrcamentos()

    @Query("DELETE FROM faturas")
    suspend fun clearFaturas()
    
    @Query("DELETE FROM historico_comunicacao")
    suspend fun clearHistoricos()
    
    @Transaction
    suspend fun clearAllTables() {
        clearUsuarios()
        clearClientes()
        clearProjetos()
        clearOrcamentos()
        clearFaturas()
        clearHistoricos()
    }
}
