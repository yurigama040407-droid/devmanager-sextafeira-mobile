package com.example.devmanagerweb.data.repository

import com.example.devmanagerweb.data.local.room.DevManagerDao
import com.example.devmanagerweb.data.local.room.DevManagerDatabase
import com.example.devmanagerweb.data.local.room.UsuarioEntity
import com.example.devmanagerweb.data.local.room.ClienteEntity
import com.example.devmanagerweb.data.local.room.ProjetoEntity
import com.example.devmanagerweb.data.local.room.OrcamentoEntity
import com.example.devmanagerweb.data.local.room.FaturaEntity
import com.example.devmanagerweb.data.remote.DevManagerApi
import com.example.devmanagerweb.domain.model.*
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstRepositoryImpl @Inject constructor(
    private val dao: DevManagerDao,
    private val api: DevManagerApi,
    private val database: DevManagerDatabase
) : DevManagerRepository {

    override suspend fun login(email: String, senha: String): Result<Usuario> {
        return try {
            val res = api.login(mapOf("email" to email, "senha" to senha))
            dao.insertUsuario(UsuarioEntity(res.id, res.nome, res.email, res.perfil, res.especialidade))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(nome: String, email: String, senha: String, perfil: Perfil, especialidade: String): Result<Usuario> {
        return try {
            val req = mapOf("nome" to nome, "email" to email, "senha" to senha, "perfil" to perfil.name, "especialidade" to especialidade)
            val res = api.register(req)
            dao.insertUsuario(UsuarioEntity(res.id, res.nome, res.email, res.perfil, res.especialidade))
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUsuarios(): Flow<List<Usuario>> {
        return dao.getUsuarios().map { entities ->
            entities.map { Usuario(it.id, it.nome, it.email, it.perfil, it.especialidade) }
        }
    }

    override fun getClientes(): Flow<List<Cliente>> {
        return dao.getClientes().map { entities ->
            entities.map { Cliente(it.id, it.usuarioId, it.nome, it.email, it.telefone, it.origemLead, it.ativo) }
        }
    }

    override suspend fun addCliente(cliente: Cliente): Result<Unit> {
        return try {
            api.addCliente(cliente)
            dao.insertCliente(ClienteEntity(cliente.id, cliente.usuarioId, cliente.nome, cliente.email, cliente.telefone, cliente.origemLead, cliente.ativo))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCliente(cliente: Cliente): Result<Unit> {
        return try {
            api.updateCliente(cliente.id, cliente)
            dao.insertCliente(ClienteEntity(cliente.id, cliente.usuarioId, cliente.nome, cliente.email, cliente.telefone, cliente.origemLead, cliente.ativo))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getProjetos(): Flow<List<Projeto>> {
        return dao.getProjetos().map { entities ->
            entities.map { Projeto(it.id, it.clienteId, it.titulo, it.descricao, it.tecnologias, it.prazo, it.status, it.urlEntrega, it.faseFunil) }
        }
    }

    override fun getProjetosByCliente(clienteId: String): Flow<List<Projeto>> {
        return dao.getProjetos().map { entities ->
            entities.filter { it.clienteId == clienteId }.map { Projeto(it.id, it.clienteId, it.titulo, it.descricao, it.tecnologias, it.prazo, it.status, it.urlEntrega, it.faseFunil) }
        }
    }

    override suspend fun updateProjeto(projeto: Projeto): Result<Unit> {
        return try {
            api.updateProjeto(projeto.id, projeto)
            dao.insertProjeto(ProjetoEntity(projeto.id, projeto.clienteId, projeto.titulo, projeto.descricao, projeto.tecnologias, projeto.prazo, projeto.status, projeto.urlEntrega, projeto.faseFunil))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getOrcamentos(): Flow<List<Orcamento>> {
        return dao.getOrcamentos().map { entities ->
            entities.map { Orcamento(it.id, it.projetoId, it.escopo, it.valor, it.status) }
        }
    }

    override suspend fun getOrcamentoByProjeto(projetoId: String): Orcamento? {
        return try {
            val net = api.getOrcamentos()
            net.find { it.projetoId == projetoId }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateOrcamento(orcamento: Orcamento): Result<Unit> {
        return try {
            api.updateOrcamento(orcamento.id, orcamento)
            dao.insertOrcamento(OrcamentoEntity(orcamento.id, orcamento.projetoId, orcamento.escopo, orcamento.valor, orcamento.status))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getFaturas(): Flow<List<Fatura>> {
        return dao.getFaturas().map { entities ->
            entities.map { Fatura(it.id, it.projetoId, it.valor, it.vencimento, it.status) }
        }
    }

    override suspend fun updateFatura(fatura: Fatura): Result<Unit> {
        return try {
            api.updateFatura(fatura.id, fatura)
            dao.insertFatura(FaturaEntity(fatura.id, fatura.projetoId, fatura.valor, fatura.vencimento, fatura.status))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHistoricosByCliente(clienteId: String): List<HistoricoComunicacao> {
        return emptyList()
    }

    override suspend fun addHistorico(historico: HistoricoComunicacao): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun refreshData() {
        try {
            val u = api.getUsuarios()
            u.forEach { dao.insertUsuario(UsuarioEntity(it.id, it.nome, it.email, it.perfil, it.especialidade)) }
            val c = api.getClientes()
            c.forEach { dao.insertCliente(ClienteEntity(it.id, it.usuarioId, it.nome, it.email, it.telefone, it.origemLead, it.ativo)) }
            val p = api.getProjetos()
            p.forEach { dao.insertProjeto(ProjetoEntity(it.id, it.clienteId, it.titulo, it.descricao, it.tecnologias, it.prazo, it.status, it.urlEntrega, it.faseFunil)) }
            val o = api.getOrcamentos()
            o.forEach { dao.insertOrcamento(OrcamentoEntity(it.id, it.projetoId, it.escopo, it.valor, it.status)) }
            val f = api.getFaturas()
            f.forEach { dao.insertFatura(FaturaEntity(it.id, it.projetoId, it.valor, it.vencimento, it.status)) }
        } catch (e: Exception) {
            // Offline - mantém cache
        }
    }

    override suspend fun clearSession() {
        dao.clearAllTables()
    }
}
