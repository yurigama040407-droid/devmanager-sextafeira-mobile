package com.example.devmanagerweb.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devmanagerweb.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToInvoices: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painel Financeiro", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark),
                actions = {
                    TextButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Text("Sair", color = ErrorColor)
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Cards 2x2
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Receita Recebida",
                            value = "R$ ${String.format("%.2f", state.receitaRecebida)}",
                            color = Success
                        )
                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Receita Prevista",
                            value = "R$ ${String.format("%.2f", state.receitaPrevista)}",
                            color = Primary
                        )
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Projetos Ativos",
                            value = state.projetosAtivos.toString(),
                            color = Warning
                        )
                        DashboardCard(
                            modifier = Modifier.weight(1f),
                            title = "Faturas Atrasadas",
                            value = state.faturasAtrasadas.size.toString(),
                            color = ErrorColor
                        )
                    }
                }

                if (!state.isCliente && state.faturamentoPorProjeto.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Faturamento por Projeto", color = TextPrimary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(16.dp))
                                SimpleBarChart(data = state.faturamentoPorProjeto)
                            }
                        }
                    }
                }

                item {
                    Text("Alertas", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (state.faturasAtrasadas.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                        ) {
                            Text("Nenhuma fatura em atraso.", color = Success, modifier = Modifier.padding(16.dp))
                        }
                    } else {
                        state.faturasAtrasadas.forEach { fatura ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable { onNavigateToInvoices() },
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                            ) {
                                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Fatura vencida em ${fatura.vencimento}", color = TextPrimary)
                                    Text("R$ ${fatura.valor}", color = ErrorColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(modifier: Modifier = Modifier, title: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SimpleBarChart(data: Map<String, Double>) {
    if (data.isEmpty()) return
    val maxVal = data.values.maxOrNull() ?: 1.0
    val entries = data.entries.toList()

    Canvas(modifier = Modifier.fillMaxSize().padding(top = 16.dp, bottom = 24.dp)) {
        val barWidth = size.width / (entries.size * 2)
        var xOffset = barWidth / 2

        entries.forEach { entry ->
            val barHeight = (entry.value / maxVal) * size.height
            drawRect(
                color = Primary,
                topLeft = Offset(xOffset, size.height - barHeight.toFloat()),
                size = Size(barWidth, barHeight.toFloat())
            )
            xOffset += barWidth * 2
        }
    }
}
