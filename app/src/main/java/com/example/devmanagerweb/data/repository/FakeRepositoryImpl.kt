package com.example.devmanagerweb.data.repository

import com.example.devmanagerweb.domain.model.*
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import javax.inject.Inject

class FakeRepositoryImpl @Inject constructor() : DevManagerRepository {

    private val usuarios = mutableListOf(
        Usuario("1", "Dev Admin", "admin@dev.com", Perfil.DESENVOLVEDOR, "Fullstack"),
        Usuario("2", "Cliente Acme", "cliente@acme.com", Perfil.CLIENTE)
    )

    private val clientes = mutableListOf(
        Cliente("1", "2", "Acme Corp", "cliente@acme.com", "(11) 99999-9999", "Upwork")
    )

    private val projetos = mutableListOf(
        Projeto("1", "1", "Sistema de Estoque", "React, Node, PostgreSQL", "React, Node, PostgreSQL", "02/10/2026", StatusProjeto.PLANEJAMENTO)
    )

    private val orcamentos = mutableListOf(
        Orcamento("1", "1", "Desenvolvimento Completo", 5000.0, StatusOrcamento.PENDENTE)
    )

    private val faturas = mutableListOf(
        Fatura("1", "1", 2500.0, "17/09/2026", StatusFatura.PENDENTE)
    )

    private val historicos = mutableListOf(
        HistoricoComunicacao("1", "1", "01/09/2026", "Primeiro contato pelo Upwork")
    )

    override suspend fun login(email: String, senha: String): Result<Usuario> {
        val user = usuarios.find { it.email == email }
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
        if (usuarios.any { it.email == email }) {
            return Result.failure(Exception("E-mail já cadastrado"))
        }
        val newUser = Usuario(
            id = (usuarios.size + 1).toString(),
            nome = nome,
            email = email,
            perfil = perfil,
            especialidade = especialidade
        )
        usuarios.add(newUser)
        return Result.success(newUser)
    }

    override suspend fun getUsuarios(): List<Usuario> = usuarios
    override suspend fun getClientes(): List<Cliente> = clientes
    override suspend fun addCliente(cliente: Cliente): Result<Unit> {
        clientes.add(cliente)
        return Result.success(Unit)
    }
    override suspend fun updateCliente(cliente: Cliente): Result<Unit> {
        val index = clientes.indexOfFirst { it.id == cliente.id }
        if (index != -1) {
            clientes[index] = cliente
            return Result.success(Unit)
        }
        return Result.failure(Exception("Cliente não encontrado"))
    }
    
    override suspend fun getProjetos(): List<Projeto> = projetos
    override suspend fun getProjetosByCliente(clienteId: String): List<Projeto> {
        return projetos.filter { it.clienteId == clienteId }
    }
    override suspend fun updateProjeto(projeto: Projeto): Result<Unit> {
        val index = projetos.indexOfFirst { it.id == projeto.id }
        if (index != -1) {
            projetos[index] = projeto
            return Result.success(Unit)
        }
        return Result.failure(Exception("Projeto não encontrado"))
    }
    
    override suspend fun getOrcamentos(): List<Orcamento> = orcamentos
    override suspend fun updateOrcamento(orcamento: Orcamento): Result<Unit> {
        val index = orcamentos.indexOfFirst { it.id == orcamento.id }
        if (index != -1) {
            orcamentos[index] = orcamento
            return Result.success(Unit)
        }
        return Result.failure(Exception("Orçamento não encontrado"))
    }
    
    override suspend fun getFaturas(): List<Fatura> = faturas
    override suspend fun updateFatura(fatura: Fatura): Result<Unit> {
        val index = faturas.indexOfFirst { it.id == fatura.id }
        if (index != -1) {
            faturas[index] = fatura
            return Result.success(Unit)
        }
        return Result.failure(Exception("Fatura não encontrada"))
    }
    
    override suspend fun getOrcamentoByProjeto(projetoId: String): Orcamento? {
        return orcamentos.find { it.projetoId == projetoId }
    }

    override suspend fun getHistoricosByCliente(clienteId: String): List<HistoricoComunicacao> {
        return historicos.filter { it.clienteId == clienteId }
    }

    override suspend fun addHistorico(historico: HistoricoComunicacao): Result<Unit> {
        historicos.add(historico)
        return Result.success(Unit)
    }
}
