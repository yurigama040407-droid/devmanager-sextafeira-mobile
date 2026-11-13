package com.example.devmanagerweb.ui.screens.projects

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devmanagerweb.domain.model.StatusOrcamento
import com.example.devmanagerweb.domain.model.StatusProjeto
import com.example.devmanagerweb.ui.components.CustomTextField
import com.example.devmanagerweb.ui.components.PrimaryButton
import com.example.devmanagerweb.ui.theme.*
import com.example.devmanagerweb.util.BudgetPdfGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var urlEntrega by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.projeto?.titulo ?: "Detalhes do Projeto", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = TextPrimary)
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error!!, color = ErrorColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val projeto = state.projeto!!
                val orcamento = state.orcamento

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Status: ${projeto.status.name}", color = Primary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Descrição: ${projeto.descricao}", color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tecnologias: ${projeto.tecnologias}", color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Prazo: ${projeto.prazo}", color = TextSecondary)
                        if (projeto.urlEntrega != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("URL: ${projeto.urlEntrega}", color = Success)
                        }
                    }
                }

                if (orcamento != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Orçamento", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Status: ${orcamento.status.name}", color = if(orcamento.status == StatusOrcamento.APROVADO) Success else Warning)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Valor: R$ ${String.format("%.2f", orcamento.valor)}", color = Success, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    val uri = BudgetPdfGenerator.generatePdf(context, projeto, orcamento)
                                    if (uri != null) {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(uri, "application/pdf")
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        try {
                                            context.startActivity(Intent.createChooser(intent, "Abrir Orçamento PDF"))
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Gerado em cache: $uri", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Erro ao gerar PDF", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Ver PDF do Orçamento", color = Primary)
                            }

                            // Botão de aprovação simulado (Visível se pendente)
                            if (orcamento.status == StatusOrcamento.PENDENTE) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.approveBudget() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Success),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Aprovar Orçamento", color = BackgroundDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (projeto.status == StatusProjeto.TESTES || projeto.status == StatusProjeto.EM_CODIGO) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Entrega do Projeto", color = TextPrimary, fontWeight = FontWeight.Bold)
                    CustomTextField(
                        value = urlEntrega,
                        onValueChange = { urlEntrega = it },
                        label = "Link do Repositório ou Produção"
                    )
                    PrimaryButton(
                        text = "Finalizar Projeto",
                        onClick = { viewModel.deliverProject(urlEntrega) }
                    )
                }
            }
        }
    }
}
