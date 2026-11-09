package com.example.devmanagerweb.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devmanagerweb.domain.model.Perfil
import com.example.devmanagerweb.ui.components.CustomTextField
import com.example.devmanagerweb.ui.components.PrimaryButton
import com.example.devmanagerweb.ui.theme.Primary
import com.example.devmanagerweb.ui.theme.TextSecondary

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: (Perfil) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var especialidade by remember { mutableStateOf("") }
    var selectedPerfil by remember { mutableStateOf(Perfil.DESENVOLVEDOR) }

    val authState by viewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess((authState as AuthState.Success).perfil)
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Criar Conta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            PerfilOption(
                title = "Desenvolvedor",
                selected = selectedPerfil == Perfil.DESENVOLVEDOR,
                onClick = { selectedPerfil = Perfil.DESENVOLVEDOR }
            )
            PerfilOption(
                title = "Cliente",
                selected = selectedPerfil == Perfil.CLIENTE,
                onClick = { selectedPerfil = Perfil.CLIENTE }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(
            value = nome,
            onValueChange = { nome = it },
            label = "Nome completo"
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "E-mail"
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = senha,
            onValueChange = { senha = it },
            label = "Senha",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (selectedPerfil == Perfil.DESENVOLVEDOR) {
            CustomTextField(
                value = especialidade,
                onValueChange = { especialidade = it },
                label = "Especialidade (ex: Mobile, Backend)"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Criar conta",
            onClick = { viewModel.register(nome, email, senha, selectedPerfil, especialidade) },
            isLoading = authState is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Row {
            Text(text = "Já tem uma conta? ", color = TextSecondary)
            Text(
                text = "Faça login",
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateBack() }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PerfilOption(title: String, selected: Boolean, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = title, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.clickable { onClick() })
    }
}
