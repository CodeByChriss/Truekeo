package com.chaima.truekeo.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chaima.truekeo.R
import com.chaima.truekeo.components.TruekeCard
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.managers.TruekeContainer
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import com.chaima.truekeo.navigation.NavBarRoutes
import com.chaima.truekeo.ui.theme.TruekeoTheme
import kotlinx.coroutines.launch

@Composable
fun MyTruekesTab(navController: NavController) {
    val authManager = remember { AuthContainer.authManager }
    val truekeManager = remember { TruekeContainer.truekeManager }

    val currentUserId = authManager.userProfile?.id

    var truekes by remember { mutableStateOf(emptyList<Trueke>()) }
    var isLoading by remember { mutableStateOf(true) }
    var startPage by remember { mutableStateOf<Int?>(null) }

    val pages = listOf(
        stringResource(R.string.trueke_state_open) to TruekeStatus.OPEN,
        stringResource(R.string.trueke_state_reserved) to TruekeStatus.RESERVED,
        stringResource(R.string.trueke_state_completed) to TruekeStatus.COMPLETED
    )

    // Cargar truekes al entrar (y cuando cambie el usuario)
    LaunchedEffect(authManager.userProfile?.id) {
        if (currentUserId == null) {
            truekes = emptyList()
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        truekes = truekeManager.getMyTruekes()
            .filter { it.status != TruekeStatus.CANCELLED }
            .sortedByDescending { t ->
                val u = t.updatedAt
                if (u > 0L) u else t.createdAt
            }
        isLoading = false
    }

    LaunchedEffect(isLoading, truekes) {
        if (!isLoading) {
            val priority = listOf(
                TruekeStatus.RESERVED,
                TruekeStatus.OPEN,
                TruekeStatus.COMPLETED
            )

            val targetStatus = priority.firstOrNull { status ->
                truekes.any { it.status == status }
            }

            // Si no hay ninguno => reserved (index 1)
            val targetPage = targetStatus?.let { status ->
                pages.indexOfFirst { it.second == status }
            } ?: 1

            startPage = if (targetPage >= 0) targetPage else 1
        }
    }

    val scope = rememberCoroutineScope()

    TruekeoTheme(dynamicColor = false) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.my_truekes),
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.height(12.dp))

                // ✅ Mientras no sepamos qué página va primero, mostramos loader (evita "salto" visual)
                if (isLoading || startPage == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // ✅ Creamos el pagerState SOLO cuando ya sabemos startPage
                    val pagerState = rememberPagerState(
                        initialPage = startPage!!,
                        pageCount = { pages.size }
                    )

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    ) {
                        pages.forEachIndexed { index, (label, _) ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = {
                                    Text(
                                        text = label,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontFamily = FontFamily(Font(R.font.saira_medium)),
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { pageIndex ->
                            val status = pages[pageIndex].second
                            val filtered = remember(truekes, status) {
                                truekes.filter { it.status == status }
                            }

                            if (filtered.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(emptyTextForStatus(status)),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    item {
                                        HorizontalDivider(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }

                                    itemsIndexed(filtered) { index, trueke ->
                                        TruekeCard(
                                            trueke = trueke,
                                            currentUserId = currentUserId,
                                            onClick = {
                                                navController.navigate(
                                                    NavBarRoutes.TruekeDetails.create(trueke.id)
                                                )
                                            }
                                        )

                                        if (index < filtered.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = MaterialTheme.colorScheme.outlineVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun emptyTextForStatus(status: TruekeStatus): Int =
    when (status) {
        TruekeStatus.OPEN -> R.string.empty_open_truekes
        TruekeStatus.RESERVED -> R.string.empty_reserved_truekes
        TruekeStatus.COMPLETED -> R.string.empty_completed_truekes
        TruekeStatus.CANCELLED -> R.string.empty_open_truekes
    }