package com.example.devmanagerweb.domain.usecase

import com.example.devmanagerweb.domain.model.*
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import javax.inject.Inject

class RN003InativarClienteUseCase @Inject constructor(
    private val repository: DevManagerRepository
) {
    suspend operator fun invoke(clienteId: String): Result<Unit> {
        // RN003 Não excluir cliente com projeto "Em Andamento" ou fatura pendente; apenas inativar.
        val projetos = repository.getProjetosByCliente(clienteId)
        val hasActiveProject = projetos.any { it.status != StatusProjeto.ENTREGUE }
        
        val faturas = repository.getFaturas()
        val faturasDoCliente = faturas.filter { fatura ->
            projetos.any { it.id == fatura.projetoId }
        }
        val hasPendingInvoice = faturasDoCliente.any { 
            it.status == StatusFatura.PENDENTE || it.status == StatusFatura.ATRASADA 
        }

        if (hasActiveProject || hasPendingInvoice) {
            return Result.failure(Exception("Não é possível excluir/inativar: O cliente possui projetos ativos ou faturas pendentes."))
        }

        val clientes = repository.getClientes()
        val cliente = clientes.find { it.id == clienteId } 
            ?: return Result.failure(Exception("Cliente não encontrado"))

        return repository.updateCliente(cliente.copy(ativo = false))
    }
}

class RN001AprovarOrcamentoUseCase @Inject constructor(
    private val repository: DevManagerRepository
) {
    suspend operator fun invoke(orcamentoId: String): Result<Unit> {
        val orcamentos = repository.getOrcamentos()
        val orcamento = orcamentos.find { it.id == orcamentoId } 
            ?: return Result.failure(Exception("Orçamento não encontrado"))
            
        val updateOrcamentoRes = repository.updateOrcamento(orcamento.copy(status = StatusOrcamento.APROVADO))
        if(updateOrcamentoRes.isFailure) return updateOrcamentoRes

        val projetos = repository.getProjetos()
        val projeto = projetos.find { it.id == orcamento.projetoId }
            ?: return Result.failure(Exception("Projeto vinculado não encontrado"))
        
        // RN001: Move para "Em Código" após aprovação
        return repository.updateProjeto(projeto.copy(status = StatusProjeto.EM_CODIGO))
    }
}

class RN004EntregarProjetoUseCase @Inject constructor(
    private val repository: DevManagerRepository
) {
    suspend operator fun invoke(projetoId: String, urlEntrega: String): Result<Unit> {
        if (urlEntrega.isBlank()) {
            return Result.failure(Exception("Forneça a URL de entrega ou link do repositório."))
        }
        
        val projetos = repository.getProjetos()
        val projeto = projetos.find { it.id == projetoId }
            ?: return Result.failure(Exception("Projeto não encontrado"))

        // RN004: Muda status para ENTREGUE com URL preenchida
        return repository.updateProjeto(projeto.copy(status = StatusProjeto.ENTREGUE, urlEntrega = urlEntrega))
    }
}
