package com.example.devmanagerweb.data.repository

import com.example.devmanagerweb.data.remote.DevManagerApi
import com.example.devmanagerweb.domain.model.*
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val api: DevManagerApi
) : DevManagerRepository {
    override suspend fun login(email: String, senha: String): Result<Usuario> = try {
        Result.success(api.login(mapOf("email" to email, "senha" to senha)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun register(nome: String, email: String, senha: String, perfil: Perfil, especialidade: String): Result<Usuario> = try {
        val req = mapOf("nome" to nome, "email" to email, "senha" to senha, "perfil" to perfil.name, "especialidade" to especialidade)
        Result.success(api.register(req))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUsuarios(): List<Usuario> = try { api.getUsuarios() } catch (e: Exception) { emptyList() }
    override suspend fun getClientes(): List<Cliente> = try { api.getClientes() } catch (e: Exception) { emptyList() }
    override suspend fun addCliente(cliente: Cliente): Result<Unit> = try {
        api.addCliente(cliente)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun updateCliente(cliente: Cliente): Result<Unit> = try {
        api.updateCliente(cliente.id, cliente)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getProjetos(): List<Projeto> = try { api.getProjetos() } catch (e: Exception) { emptyList() }
    override suspend fun getProjetosByCliente(clienteId: String): List<Projeto> = try {
        api.getProjetos().filter { it.clienteId == clienteId }
    } catch (e: Exception) { emptyList() }

    override suspend fun updateProjeto(projeto: Projeto): Result<Unit> = try {
        api.updateProjeto(projeto.id, projeto)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getOrcamentos(): List<Orcamento> = try { api.getOrcamentos() } catch (e: Exception) { emptyList() }
    override suspend fun updateOrcamento(orcamento: Orcamento): Result<Unit> = try {
        api.updateOrcamento(orcamento.id, orcamento)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getFaturas(): List<Fatura> = try { api.getFaturas() } catch (e: Exception) { emptyList() }
    override suspend fun updateFatura(fatura: Fatura): Result<Unit> = try {
        api.updateFatura(fatura.id, fatura)
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
