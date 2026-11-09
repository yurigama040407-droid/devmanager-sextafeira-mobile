package com.example.devmanagerweb.ui.screens.funnel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devmanagerweb.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FunnelScreen(
    viewModel: FunnelViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { viewModel.fases.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Funil de Vendas", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs/Chips
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = BackgroundDark,
                contentColor = Primary,
                edgePadding = 16.dp,
                divider = {}
            ) {
                viewModel.fases.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title, fontWeight = FontWeight.Bold) },
                        selectedContentColor = Primary,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.error!!, color = ErrorColor)
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val faseAtual = viewModel.fases[page]
                    val cardsDaFase = state.cards.filter { it.projeto.faseFunil == faseAtual }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cardsDaFase) { cardData ->
                            FunnelCard(
                                data = cardData,
                                isLastPhase = page == viewModel.fases.size - 1,
                                onMoveToNext = { viewModel.moveToNextPhase(cardData.projeto.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FunnelCard(data: FunnelCardData, isLastPhase: Boolean, onMoveToNext: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = data.projeto.titulo, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Cliente: ${data.cliente.nome}", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Orçamento: R$ ${String.format("%.2f", data.valorOrcamento)}", color = Success, fontWeight = FontWeight.Bold)
            
            if (!isLastPhase) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onMoveToNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mover para próxima fase", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Avançar", tint = BackgroundDark)
                }
            }
        }
    }
}
