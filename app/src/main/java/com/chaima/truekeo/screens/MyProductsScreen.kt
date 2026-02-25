package com.chaima.truekeo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.chaima.truekeo.R
import com.chaima.truekeo.managers.ItemContainer
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemStatus
import com.chaima.truekeo.navigation.NavBarRoutes
import com.chaima.truekeo.ui.theme.TruekeoTheme

@Composable
fun MyProductsScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(navController.currentBackStackEntry) {
        isLoading = true
        items = ItemContainer.itemManager.getMyItems()
        isLoading = false
    }

    val filteredItems = items.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    TruekeoTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.my_products),
                fontSize = 32.sp,
                fontFamily = FontFamily(Font(R.font.saira_medium)),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                if (items.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.no_products_yet),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily(Font(R.font.saira_medium))
                            )
                            Text(
                                text = stringResource(R.string.no_products_yet_subtitle),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    navController.navigate(NavBarRoutes.CreateProduct.route)
                                }
                            )
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.search_product)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredItems) { item ->
                            MyItemRow(
                                item = item,
                                onClick = {
                                    navController.navigate(
                                        NavBarRoutes.ProductDetails.create(item.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyItemRow(
    item: Item,
    onClick: () -> Unit
) {
    TruekeoTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onClick() }
                .padding(8.dp)
        ) {

            ItemStatusBadge(
                status = item.status,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val imageUrl = item.imageUrls.firstOrNull()

                AsyncImage(
                    model = imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(78.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = item.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    item.details?.let {
                        Text(
                            text = it,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemStatusBadge(
    status: ItemStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        ItemStatus.AVAILABLE -> Color(0xFF5FBAA8)
        ItemStatus.RESERVED -> Color(0xFFDEC786)
        ItemStatus.EXCHANGED -> Color(0xFFA9A8A8)
    }

    TruekeoTheme {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(color)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = stringResource(status.getStringResource()),
                fontSize = 10.sp,
                color = Color.White
            )
        }
    }
}