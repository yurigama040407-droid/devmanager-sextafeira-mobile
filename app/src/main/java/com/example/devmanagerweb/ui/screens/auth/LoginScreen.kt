package com.example.devmanagerweb.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.devmanagerweb.ui.components.DevManagerLogoHeader
import com.example.devmanagerweb.ui.components.PrimaryButton
import com.example.devmanagerweb.ui.theme.Primary
import com.example.devmanagerweb.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: (Perfil) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess((authState as AuthState.Success).perfil)
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DevManagerLogoHeader()
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Faça login para continuar",
            color = TextSecondary,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(28.dp))

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "E-mail"
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Senha",
            isPassword = true
        )

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryButton(
            text = "Entrar na Plataforma",
            onClick = { viewModel.login(email, password) },
            isLoading = authState is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        Row {
            Text(text = "Ainda não tem conta? ", color = TextSecondary)
            Text(
                text = "Registre-se",
                color = Primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
