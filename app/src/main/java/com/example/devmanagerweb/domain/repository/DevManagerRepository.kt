package com.example.devmanagerweb.domain.repository

import com.example.devmanagerweb.domain.model.*
import kotlinx.coroutines.flow.Flow

interface DevManagerRepository {
    suspend fun login(email: String, senha: String): Result<Usuario>
    suspend fun register(nome: String, email: String, senha: String, perfil: Perfil, especialidade: String): Result<Usuario>
    
    fun getUsuarios(): Flow<List<Usuario>>
    fun getClientes(): Flow<List<Cliente>>
    suspend fun addCliente(cliente: Cliente): Result<Unit>
    suspend fun updateCliente(cliente: Cliente): Result<Unit>
    
    fun getProjetos(): Flow<List<Projeto>>
    fun getProjetosByCliente(clienteId: String): Flow<List<Projeto>>
    suspend fun updateProjeto(projeto: Projeto): Result<Unit>
    
    fun getOrcamentos(): Flow<List<Orcamento>>
    suspend fun getOrcamentoByProjeto(projetoId: String): Orcamento?
    suspend fun updateOrcamento(orcamento: Orcamento): Result<Unit>
    
    fun getFaturas(): Flow<List<Fatura>>
    suspend fun updateFatura(fatura: Fatura): Result<Unit>
    
    suspend fun getHistoricosByCliente(clienteId: String): List<HistoricoComunicacao>
    suspend fun addHistorico(historico: HistoricoComunicacao): Result<Unit>
    
    suspend fun refreshData() // Força atualização com o backend
    suspend fun clearSession() // Limpa banco local e DataStore no logout
}
