package com.example.devmanagerweb.ui.screens.clients

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devmanagerweb.ui.components.CustomTextField
import com.example.devmanagerweb.ui.components.PrimaryButton
import com.example.devmanagerweb.ui.theme.BackgroundDark
import com.example.devmanagerweb.ui.theme.ErrorColor
import com.example.devmanagerweb.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddClientViewModel = hiltViewModel()
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var origemLead by remember { mutableStateOf("") }

    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Cliente", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(value = nome, onValueChange = { nome = it }, label = "Nome da Empresa / Contato")
            CustomTextField(value = email, onValueChange = { email = it }, label = "E-mail")
            CustomTextField(value = telefone, onValueChange = { telefone = it }, label = "Telefone")
            CustomTextField(value = origemLead, onValueChange = { origemLead = it }, label = "Origem (Ex: Upwork, Indicação)")

            if (state.error != null) {
                Text(state.error!!, color = ErrorColor)
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Salvar Cliente",
                onClick = { viewModel.addClient(nome, email, telefone, origemLead) },
                isLoading = state.isLoading
            )
        }
    }
}
