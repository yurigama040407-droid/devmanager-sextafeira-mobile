package com.example.devmanagerweb.ui.screens.projects

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.domain.model.Orcamento
import com.example.devmanagerweb.domain.model.Projeto
import com.example.devmanagerweb.domain.model.StatusOrcamento
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import com.example.devmanagerweb.domain.usecase.RN001AprovarOrcamentoUseCase
import com.example.devmanagerweb.domain.usecase.RN004EntregarProjetoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectDetailState(
    val isLoading: Boolean = true,
    val projeto: Projeto? = null,
    val orcamento: Orcamento? = null,
    val error: String? = null
)

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val repository: DevManagerRepository,
    private val aprovarOrcamentoUseCase: RN001AprovarOrcamentoUseCase,
    private val entregarProjetoUseCase: RN004EntregarProjetoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val projectId: String = checkNotNull(savedStateHandle["projectId"])

    private val _uiState = MutableStateFlow(ProjectDetailState())
    val uiState: StateFlow<ProjectDetailState> = _uiState.asStateFlow()

    init {
        loadProject()
    }

    private fun loadProject() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                repository.getProjetos().collect { projetos ->
                    val projeto = projetos.find { it.id == projectId }
                    val orcamento = repository.getOrcamentoByProjeto(projectId)
                    
                    if (projeto != null) {
                        _uiState.value = _uiState.value.copy(isLoading = false, projeto = projeto, orcamento = orcamento)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "Projeto não encontrado")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erro ao carregar projeto")
            }
        }
    }

    fun approveBudget() {
        val orcamentoId = _uiState.value.orcamento?.id ?: return
        viewModelScope.launch {
            val result = aprovarOrcamentoUseCase(orcamentoId)
            if (result.isSuccess) {
                loadProject() // Reload to reflect status changes
            } else {
                _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun deliverProject(url: String) {
        viewModelScope.launch {
            val result = entregarProjetoUseCase(projectId, url)
            if (result.isSuccess) {
                loadProject()
            } else {
                _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }
}
