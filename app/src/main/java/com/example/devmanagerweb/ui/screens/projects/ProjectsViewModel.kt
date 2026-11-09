package com.example.devmanagerweb.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devmanagerweb.domain.model.Projeto
import com.example.devmanagerweb.domain.repository.DevManagerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectsState(
    val isLoading: Boolean = true,
    val projects: List<Projeto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val repository: DevManagerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsState())
    val uiState: StateFlow<ProjectsState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val projects = repository.getProjetos()
                _uiState.value = _uiState.value.copy(isLoading = false, projects = projects)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Erro ao carregar projetos")
            }
        }
    }
}
