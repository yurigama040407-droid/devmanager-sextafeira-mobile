package com.example.devmanagerweb.ui.screens.funnel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.domain.model.Cliente
import com.example.devmanagerweb.domain.model.Projeto
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FunnelCardData(
    val projeto: Projeto,
    val cliente: Cliente,
    val valorOrcamento: Double
)

data class FunnelState(
    val isLoading: Boolean = true,
    val cards: List<FunnelCardData> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FunnelViewModel @Inject constructor(
    private val repository: DevManagerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FunnelState())
    val uiState: StateFlow<FunnelState> = _uiState.asStateFlow()

    val fases = listOf("Contato Inicial", "Em Negociação", "Aguardando Aprovação", "Projeto Fechado")

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val projetos = repository.getProjetos()
                val clientes = repository.getClientes()
                val orcamentos = repository.getOrcamentos()

                val funnelCards = projetos.mapNotNull { p ->
                    val cliente = clientes.find { it.id == p.clienteId }
                    val orcamento = orcamentos.find { it.projetoId == p.id }
                    if (cliente != null) {
                        FunnelCardData(p, cliente, orcamento?.valor ?: 0.0)
                    } else null
                }
                _uiState.value = _uiState.value.copy(isLoading = false, cards = funnelCards)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erro ao carregar funil")
            }
        }
    }

    fun moveToNextPhase(projetoId: String) {
        viewModelScope.launch {
            val projeto = repository.getProjetos().find { it.id == projetoId } ?: return@launch
            val currentIndex = fases.indexOf(projeto.faseFunil)
            if (currentIndex in 0 until (fases.size - 1)) {
                val nextFase = fases[currentIndex + 1]
                val result = repository.updateProjeto(projeto.copy(faseFunil = nextFase))
                if (result.isSuccess) {
                    loadData()
                }
            }
        }
    }
}
