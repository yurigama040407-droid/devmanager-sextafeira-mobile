package com.example.devmanagerweb.data.repository

import com.example.devmanagerweb.domain.model.*
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeRepositoryImpl @Inject constructor() : DevManagerRepository {

    private val usuarios = MutableStateFlow(listOf(
        Usuario("1", "Dev Admin", "admin@dev.com", Perfil.DESENVOLVEDOR, "Fullstack"),
        Usuario("2", "Cliente Acme", "cliente@acme.com", Perfil.CLIENTE)
    ))

    private val clientes = MutableStateFlow(listOf(
        Cliente("1", "2", "Acme Corp", "cliente@acme.com", "(11) 99999-9999", "Upwork")
    ))

    private val projetos = MutableStateFlow(listOf(
        Projeto("1", "1", "Sistema de Estoque", "React, Node, PostgreSQL", "React, Node, PostgreSQL", "02/10/2026", StatusProjeto.PLANEJAMENTO)
    ))

    private val orcamentos = MutableStateFlow(listOf(
        Orcamento("1", "1", "Desenvolvimento Completo", 5000.0, StatusOrcamento.PENDENTE)
    ))

    private val faturas = MutableStateFlow(listOf(
        Fatura("1", "1", 2500.0, "17/09/2026", StatusFatura.PENDENTE)
    ))

    private val historicos = mutableListOf(
        HistoricoComunicacao("1", "1", "01/09/2026", "Primeiro contato pelo Upwork")
    )

    override suspend fun login(email: String, senha: String): Result<Usuario> {
        val user = usuarios.value.find { it.email == email }
        return if (user != null && senha == "123456") {
            Result.success(user)
        } else {
            Result.failure(Exception("Credenciais inválidas"))
        }
    }

    override suspend fun register(
        nome: String,
        email: String,
        senha: String,
        perfil: Perfil,
        especialidade: String
    ): Result<Usuario> {
        val currentList = usuarios.value
        if (currentList.any { it.email == email }) {
            return Result.failure(Exception("E-mail já cadastrado"))
        }
        val newUser = Usuario(
            id = (currentList.size + 1).toString(),
            nome = nome,
            email = email,
            perfil = perfil,
            especialidade = especialidade
        )
        usuarios.value = currentList + newUser
        return Result.success(newUser)
    }

    override fun getUsuarios(): Flow<List<Usuario>> = usuarios

    override fun getClientes(): Flow<List<Cliente>> = clientes

    override suspend fun addCliente(cliente: Cliente): Result<Unit> {
        clientes.value = clientes.value + cliente
        return Result.success(Unit)
    }

    override suspend fun updateCliente(cliente: Cliente): Result<Unit> {
        val currentList = clientes.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == cliente.id }
        if (index != -1) {
            currentList[index] = cliente
            clientes.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Cliente não encontrado"))
    }

    override fun getProjetos(): Flow<List<Projeto>> = projetos

    override fun getProjetosByCliente(clienteId: String): Flow<List<Projeto>> {
        return projetos.map { list -> list.filter { it.clienteId == clienteId } }
    }

    override suspend fun updateProjeto(projeto: Projeto): Result<Unit> {
        val currentList = projetos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == projeto.id }
        if (index != -1) {
            currentList[index] = projeto
            projetos.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Projeto não encontrado"))
    }

    override fun getOrcamentos(): Flow<List<Orcamento>> = orcamentos

    override suspend fun updateOrcamento(orcamento: Orcamento): Result<Unit> {
        val currentList = orcamentos.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == orcamento.id }
        if (index != -1) {
            currentList[index] = orcamento
            orcamentos.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Orçamento não encontrado"))
    }

    override fun getFaturas(): Flow<List<Fatura>> = faturas

    override suspend fun updateFatura(fatura: Fatura): Result<Unit> {
        val currentList = faturas.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == fatura.id }
        if (index != -1) {
            currentList[index] = fatura
            faturas.value = currentList
            return Result.success(Unit)
        }
        return Result.failure(Exception("Fatura não encontrada"))
    }

    override suspend fun getOrcamentoByProjeto(projetoId: String): Orcamento? {
        return orcamentos.value.find { it.projetoId == projetoId }
    }

    override suspend fun getHistoricosByCliente(clienteId: String): List<HistoricoComunicacao> {
        return historicos.filter { it.clienteId == clienteId }
    }

    override suspend fun addHistorico(historico: HistoricoComunicacao): Result<Unit> {
        historicos.add(historico)
        return Result.success(Unit)
    }
    
    override suspend fun refreshData() {
        // No Fake, refresh não faz nada com a rede.
    }
    
    override suspend fun clearSession() {
        // No Fake, apenas limpamos os fluxos que não são os dados padrão
        // Para manter o demo viável, vamos manter os dados iniciais.
    }
}
