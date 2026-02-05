package com.chaima.truekeo.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaima.truekeo.R
enum class ProductStatus {
    AVAILABLE,
    RESERVED,
    EXCHANGED
}

data class MyProduct(
    val name: String,
    val subtitle: String,
    val description: String,
    val imageRes: Int,
    val status: ProductStatus
)
@Composable
fun MyProductsScreen() {

    var searchQuery by remember { mutableStateOf("") }

    val products = remember {
        listOf(
            MyProduct(
                name = "Guitarra Clásica",
                subtitle = "Guitarra Gibson 233E",
                description = "Guitarra Gibson 233E en excelente estado",
                imageRes = R.drawable.guitarra,
                status = ProductStatus.AVAILABLE
            ),
            MyProduct(
                name = "iPhone 12",
                subtitle = "128GB Negro",
                description = "Sin golpes, batería al 90%",
                imageRes = R.drawable.phone,
                status = ProductStatus.RESERVED
            ),
            MyProduct(
                name = "Bicicleta MTB",
                subtitle = "Rockrider 520",
                description = "Usada pero bien cuidada",
                imageRes = R.drawable.bici,
                status = ProductStatus.EXCHANGED
            )
        )
    }

    val filteredProducts = products.filter {
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
            items(filteredProducts) { product ->
                MyProductItem(
                    product = product,
                    onClick = {
                    }
                )
            }
        }
    }
}

@Composable
fun MyProductItem(
    product: MyProduct,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color(0xFFF7F7F7))
            .clickable { onClick() }   // 👈 AQUÍ
            .padding(8.dp)
    ) {

        ProductStatusBadge(
            status = product.status,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // IMAGEN
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .size(78.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = product.subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ProductStatusBadge(
    status: ProductStatus,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        ProductStatus.AVAILABLE -> "Disponible" to Color(0xFF5FBAA8)
        ProductStatus.RESERVED -> "Reservado" to Color(0xFFDEC786)
        ProductStatus.EXCHANGED -> "Intercambiado" to Color(0xFFA9A8A8)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}
