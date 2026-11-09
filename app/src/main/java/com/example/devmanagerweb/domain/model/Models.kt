package com.example.devmanagerweb.domain.model

enum class Perfil {
    DESENVOLVEDOR,
    CLIENTE
}

data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val perfil: Perfil,
    val especialidade: String = ""
)

data class Cliente(
    val id: String,
    val usuarioId: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val origemLead: String,
    val ativo: Boolean = true
)

data class HistoricoComunicacao(
    val id: String,
    val clienteId: String,
    val data: String, // format dd/MM/yyyy
    val descricao: String
)

enum class StatusProjeto {
    PLANEJAMENTO,
    EM_CODIGO,
    TESTES,
    ENTREGUE
}

data class Projeto(
    val id: String,
    val clienteId: String,
    val titulo: String,
    val descricao: String,
    val tecnologias: String,
    val prazo: String, // format dd/MM/yyyy
    val status: StatusProjeto,
    val urlEntrega: String? = null,
    val faseFunil: String = "Contato Inicial" // Contato Inicial, Em Negociação, Aguardando Aprovação, Projeto Fechado
)

enum class StatusOrcamento {
    PENDENTE,
    APROVADO,
    REJEITADO
}

data class Orcamento(
    val id: String,
    val projetoId: String,
    val escopo: String,
    val valor: Double,
    val status: StatusOrcamento
)

enum class StatusFatura {
    PENDENTE,
    PAGA,
    ATRASADA
}

data class Fatura(
    val id: String,
    val projetoId: String,
    val valor: Double,
    val vencimento: String, // format dd/MM/yyyy
    val status: StatusFatura
)
