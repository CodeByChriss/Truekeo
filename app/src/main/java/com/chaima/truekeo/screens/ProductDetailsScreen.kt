package com.chaima.truekeo.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.R
import com.chaima.truekeo.components.ImageSelectorGrid
import com.chaima.truekeo.models.ItemStatus

@Composable
fun ProductDetailsScreen(
    productName: String,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(productName) }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ItemStatus.AVAILABLE) }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imageSlotToEdit by remember { mutableStateOf<Int?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                if (uri != null && imageSlotToEdit != null) {
                    val slot = imageSlotToEdit!!

                    imageUris =
                        if (slot < imageUris.size) {
                            imageUris.toMutableList().also { it[slot] = uri }
                        } else {
                            imageUris + uri
                        }

                    imageSlotToEdit = null
                }
            }
        )

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
                Icon(Icons.Rounded.ArrowBack, contentDescription = null)
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

            ImageSelectorGrid(
                images = imageUris,
                maxImages = 5,
                onAddImage = { slot: Int ->
                    imageSlotToEdit = slot
                    imagePickerLauncher.launch("image/*")
                },
                onRemoveImage = { index: Int ->
                    imageUris = imageUris.toMutableList().also {
                        it.removeAt(index)
                    }
                }
            )

            OutlinedTextField(
                value = title,
                onValueChange = { newValue: String -> title = newValue },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { newValue: String -> description = newValue },
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
                ItemStatus.entries.forEach { itemStatus: ItemStatus ->
                    FilterChip(
                        selected = status == itemStatus,
                        onClick = { status = itemStatus },
                        label = {
                            Text(
                                when (itemStatus) {
                                    ItemStatus.AVAILABLE -> "Disponible"
                                    ItemStatus.RESERVED -> "Reservado"
                                    ItemStatus.EXCHANGED -> "Intercambiado"
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
