package com.chaima.truekeo.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.R
import com.chaima.truekeo.components.ImageSelectorGrid
import com.chaima.truekeo.managers.ImageStorageManager
import com.chaima.truekeo.managers.ItemContainer
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemStatus
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val itemManager = ItemContainer.itemManager

    var isLoading by remember { mutableStateOf(true) }
    var currentItem by remember { mutableStateOf<Item?>(null) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ItemStatus.AVAILABLE) }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imageSlotToEdit by remember { mutableStateOf<Int?>(null) }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(productId) {
        currentItem = itemManager.getItemById(productId)

        currentItem?.let {
            title = it.name
            description = it.details ?: ""
            status = it.status
        }

        isLoading = false
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                onAddImage = { slot ->
                    imageSlotToEdit = slot
                    imagePickerLauncher.launch("image/*")
                },
                onRemoveImage = { index ->
                    imageUris = imageUris.toMutableList().also { it.removeAt(index) }
                }
            )

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
                ItemStatus.entries.forEach { itemStatus ->
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
                    scope.launch {
                        val imageStorage = ImageStorageManager(context)
                        val imageUrls =
                            if (imageUris.isNotEmpty()) {
                                imageStorage.uploadItemImages(productId, imageUris)
                            } else {
                                currentItem?.imageUrls ?: emptyList()
                            }

                        val updatedItem = currentItem!!.copy(
                            name = title.trim(),
                            details = description.trim(),
                            status = status,
                            imageUrls = imageUrls
                        )

                        ItemContainer.itemManager.updateItem(updatedItem)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar cambios")
            }

            Button(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Eliminar producto")
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val result = ItemContainer.itemManager.deleteItem(productId)
                        if (result.isSuccess) {
                            onBack()
                        } else {
                            snackMessage = result.exceptionOrNull()?.message ?: "Error al eliminar"
                        }
                        showDeleteConfirm = false
                    }
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

