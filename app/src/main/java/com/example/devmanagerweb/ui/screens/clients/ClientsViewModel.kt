package com.example.devmanagerweb.ui.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.domain.model.Cliente
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import com.example.devmanagerweb.domain.usecase.RN003InativarClienteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientsState(
    val isLoading: Boolean = true,
    val clients: List<Cliente> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val repository: DevManagerRepository,
    private val inativarClienteUseCase: RN003InativarClienteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientsState())
    val uiState: StateFlow<ClientsState> = _uiState.asStateFlow()

    init {
        loadClients()
    }

    fun loadClients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val clients = repository.getClientes()
                _uiState.value = _uiState.value.copy(isLoading = false, clients = clients)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erro ao carregar clientes")
            }
        }
    }

    fun inactivateClient(clientId: String) {
        viewModelScope.launch {
            val result = inativarClienteUseCase(clientId)
            if (result.isSuccess) {
                loadClients() // reload to show changes
            } else {
                _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }
}
