package com.example.devmanagerweb.ui.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.data.local.SessionManager
import com.example.devmanagerweb.domain.model.Cliente
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AddClientState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddClientViewModel @Inject constructor(
    private val repository: DevManagerRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddClientState())
    val uiState: StateFlow<AddClientState> = _uiState.asStateFlow()

    fun addClient(nome: String, email: String, telefone: String, origemLead: String) {
        if (nome.isBlank() || email.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Nome e E-mail são obrigatórios")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val usuarioId = sessionManager.fetchUserId() ?: ""
            val newClient = Cliente(
                id = UUID.randomUUID().toString(),
                usuarioId = usuarioId,
                nome = nome,
                email = email,
                telefone = telefone,
                origemLead = origemLead
            )
            val result = repository.addCliente(newClient)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = result.exceptionOrNull()?.message ?: "Erro ao salvar")
            }
        }
    }
}
