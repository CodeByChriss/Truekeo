package com.chaima.truekeo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.chaima.truekeo.R
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.models.ItemStatus

@Composable
fun MyProductsScreen(navController: NavController) {

    var searchQuery by remember { mutableStateOf("") }

    val items = remember {
        listOf(
            Item(
                id = "1",
                name = "Guitarra Gibson 233E",
                details = "Guitarra Gibson 233E en excelente estado",
                condition = ItemCondition.GOOD,
                status = ItemStatus.AVAILABLE
            ),
            Item(
                id = "2",
                name = "iPhone 12 256GB Negro",
                details = "Sin golpes, batería al 90%",
                condition = ItemCondition.LIKE_NEW,
                status = ItemStatus.RESERVED
            ),
            Item(
                id = "3",
                name = "Bicicleta MTB Rockrider 520",
                details = "Usada pero bien cuidada",
                condition = ItemCondition.FAIR,
                status = ItemStatus.EXCHANGED
            )
        )
    }

    val filteredItems = items.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ) {

        Text(
            text = stringResource(R.string.my_products),
            fontSize = 32.sp,
            fontFamily = FontFamily(Font(R.font.saira_medium)),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                        navController.navigate("product_details/${item.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun MyItemRow(
    item: Item,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .background(Color(0xFFF7F7F7))
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

            AsyncImage(
                model = item.imageUrls.first(),
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
                        color = Color.DarkGray,
                        maxLines = 1
                    )
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