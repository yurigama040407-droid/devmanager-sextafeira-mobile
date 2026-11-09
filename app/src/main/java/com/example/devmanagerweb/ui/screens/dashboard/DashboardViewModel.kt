package com.example.devmanagerweb.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.data.local.SessionManager
import com.example.devmanagerweb.domain.model.*
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val isLoading: Boolean = true,
    val receitaRecebida: Double = 0.0,
    val receitaPrevista: Double = 0.0,
    val projetosAtivos: Int = 0,
    val faturasAtrasadas: List<Fatura> = emptyList(),
    val faturamentoPorProjeto: Map<String, Double> = emptyMap(),
    val isCliente: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DevManagerRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val perfil = sessionManager.fetchUserProfile()
                val isCliente = perfil == Perfil.CLIENTE.name

                val projetos = repository.getProjetos()
                val faturas = repository.getFaturas()
                
                // Calculos Simples
                val recebida = faturas.filter { it.status == StatusFatura.PAGA }.sumOf { it.valor }
                val prevista = faturas.filter { it.status != StatusFatura.PAGA }.sumOf { it.valor }
                val ativos = projetos.count { it.status != StatusProjeto.ENTREGUE }
                val atrasadas = faturas.filter { it.status == StatusFatura.ATRASADA }
                
                val faturamentoMap = projetos.associate { p -> 
                    p.titulo to faturas.filter { it.projetoId == p.id }.sumOf { it.valor }
                }

                _uiState.value = DashboardState(
                    isLoading = false,
                    receitaRecebida = recebida,
                    receitaPrevista = prevista,
                    projetosAtivos = ativos,
                    faturasAtrasadas = atrasadas,
                    faturamentoPorProjeto = faturamentoMap,
                    isCliente = isCliente
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erro ao carregar dados")
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }
}
