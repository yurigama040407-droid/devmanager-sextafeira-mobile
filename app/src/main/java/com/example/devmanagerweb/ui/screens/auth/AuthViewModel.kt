package com.example.devmanagerweb.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.data.local.SessionManager
import com.example.devmanagerweb.domain.model.Perfil
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val perfil: Perfil) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: DevManagerRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _authState.value = AuthState.Error("Preencha todos os campos.")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.login(email, senha)
            result.onSuccess { user ->
                sessionManager.saveUserId(user.id)
                sessionManager.saveUserProfile(user.perfil.name)
                sessionManager.saveAuthToken("fake_jwt_token_for_${user.id}")
                _authState.value = AuthState.Success(user.perfil)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Erro ao fazer login")
            }
        }
    }

    fun register(nome: String, email: String, senha: String, perfil: Perfil, especialidade: String) {
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            _authState.value = AuthState.Error("Preencha os campos obrigatórios.")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.register(nome, email, senha, perfil, especialidade)
            result.onSuccess { user ->
                sessionManager.saveUserId(user.id)
                sessionManager.saveUserProfile(user.perfil.name)
                sessionManager.saveAuthToken("fake_jwt_token_for_${user.id}")
                _authState.value = AuthState.Success(user.perfil)
            }.onFailure { error ->
                _authState.value = AuthState.Error(error.message ?: "Erro ao registrar conta")
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _authState.value = AuthState.Idle
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
