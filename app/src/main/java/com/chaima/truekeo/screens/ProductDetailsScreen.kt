package com.chaima.truekeo.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.R

@Composable
fun ProductDetailsScreen(
    productName: String,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(productName) }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ProductStatus.AVAILABLE) }

    var itemImageUri by remember { mutableStateOf<Uri?>(null) }

    val pickItemImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) itemImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, null)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily(Font(R.font.saira_semibold)),
                maxLines = 1
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    .clickable {
                        pickItemImageLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.guitarra),
                    contentDescription = "Imagen del producto",
                    modifier = Modifier.fillMaxSize()
                )

                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text(
                text = "Estado",
                style = MaterialTheme.typography.titleMedium
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProductStatus.entries.forEach { productStatus ->
                    FilterChip(
                        selected = status == productStatus,
                        onClick = { status = productStatus },
                        label = {
                            Text(
                                when (productStatus) {
                                    ProductStatus.AVAILABLE -> "Disponible"
                                    ProductStatus.RESERVED -> "Reservado"
                                    ProductStatus.EXCHANGED -> "Intercambiado"
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar cambios")
            }
        }
    }
}
