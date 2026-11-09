package com.example.devmanagerweb.domain.repository

import com.example.devmanagerweb.domain.model.*

interface DevManagerRepository {
    suspend fun login(email: String, senha: String): Result<Usuario>
    suspend fun register(nome: String, email: String, senha: String, perfil: Perfil, especialidade: String): Result<Usuario>
    
    suspend fun getUsuarios(): List<Usuario>
    suspend fun getClientes(): List<Cliente>
    suspend fun addCliente(cliente: Cliente): Result<Unit>
    suspend fun updateCliente(cliente: Cliente): Result<Unit>
    
    suspend fun getProjetos(): List<Projeto>
    suspend fun getProjetosByCliente(clienteId: String): List<Projeto>
    suspend fun updateProjeto(projeto: Projeto): Result<Unit>
    
    suspend fun getOrcamentos(): List<Orcamento>
    suspend fun getOrcamentoByProjeto(projetoId: String): Orcamento?
    suspend fun updateOrcamento(orcamento: Orcamento): Result<Unit>
    
    suspend fun getFaturas(): List<Fatura>
    suspend fun updateFatura(fatura: Fatura): Result<Unit>
    
    suspend fun getHistoricosByCliente(clienteId: String): List<HistoricoComunicacao>
    suspend fun addHistorico(historico: HistoricoComunicacao): Result<Unit>
}
