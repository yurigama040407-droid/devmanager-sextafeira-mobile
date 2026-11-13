package com.example.devmanagerweb.ui.screens.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.domain.model.Cliente
import com.example.devmanagerweb.domain.model.HistoricoComunicacao
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class ClientDetailState(
    val isLoading: Boolean = true,
    val cliente: Cliente? = null,
    val historicos: List<HistoricoComunicacao> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ClientDetailViewModel @Inject constructor(
    private val repository: DevManagerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val clientId: String = checkNotNull(savedStateHandle["clientId"])

    private val _uiState = MutableStateFlow(ClientDetailState())
    val uiState: StateFlow<ClientDetailState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getClientes().collect { clientes ->
                    val cliente = clientes.find { it.id == clientId }
                    val historicos = repository.getHistoricosByCliente(clientId)
                    
                    if (cliente != null) {
                        _uiState.value = _uiState.value.copy(isLoading = false, cliente = cliente, historicos = historicos)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "Cliente não encontrado")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erro ao carregar dados")
            }
        }
    }

    fun addHistorico(descricao: String) {
        if (descricao.isBlank()) return
        
        viewModelScope.launch {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val historico = HistoricoComunicacao(
                id = UUID.randomUUID().toString(),
                clienteId = clientId,
                data = date,
                descricao = descricao
            )
            repository.addHistorico(historico)
            loadDetails()
        }
    }
}
