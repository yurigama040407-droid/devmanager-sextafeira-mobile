package com.example.devmanagerweb.ui.screens.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.data.local.SessionManager
import com.example.devmanagerweb.domain.model.Fatura
import com.example.devmanagerweb.domain.model.Perfil
import com.example.devmanagerweb.domain.model.Projeto
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InvoiceItemData(
    val fatura: Fatura,
    val projeto: Projeto
)

data class InvoicesState(
    val isLoading: Boolean = true,
    val invoices: List<InvoiceItemData> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val repository: DevManagerRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoicesState())
    val uiState: StateFlow<InvoicesState> = _uiState.asStateFlow()

    init {
        loadInvoices()
    }

    private fun loadInvoices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val isCliente = sessionManager.fetchUserProfile() == Perfil.CLIENTE.name
                val clientId = sessionManager.fetchUserId() ?: ""
                
                val faturas = repository.getFaturas()
                val projetos = repository.getProjetos()

                val invoicesData = faturas.mapNotNull { f ->
                    val projeto = projetos.find { it.id == f.projetoId }
                    if (projeto != null) {
                        // Se for cliente, filtra só as dele
                        if (!isCliente || projeto.clienteId == clientId) {
                            InvoiceItemData(f, projeto)
                        } else null
                    } else null
                }
                
                _uiState.value = _uiState.value.copy(isLoading = false, invoices = invoicesData)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erro ao carregar faturas")
            }
        }
    }
}
