package com.chaima.truekeo.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import androidx.core.net.toUri

@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val itemManager = ItemContainer.itemManager

    var isLoading by remember { mutableStateOf(true) }
    var isLoadingSaveOrDelete by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf<Item?>(null) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ItemStatus.AVAILABLE) }

    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imageSlotToEdit by remember { mutableStateOf<Int?>(null) }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        currentItem = itemManager.getItemById(productId)
        currentItem?.let {
            title = it.name
            description = it.details ?: ""
            status = it.status
            imageUris = it.imageUrls.map { url -> url.toUri() }
        }
        isLoading = false
    }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // Simplemente se añade la nueva imagen al final de la lista existente
                imageUris = imageUris + uri
            }
        }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
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
                label = { Text(text = stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = stringResource(R.string.description)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text(
                text = stringResource(R.string.styles),
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
                                    ItemStatus.AVAILABLE -> stringResource(R.string.available)
                                    ItemStatus.RESERVED -> stringResource(R.string.reserved)
                                    ItemStatus.EXCHANGED -> stringResource(R.string.interchanged)
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
                        if (!isLoadingSaveOrDelete) {
                            isLoadingSaveOrDelete = true

                            val imageStorage = ImageStorageManager(context)

                            // Separamos las que ya son de internet de las que son nuevas
                            val remoteUrls =
                                imageUris.filter { it.scheme == "http" || it.scheme == "https" }
                            val localUris =
                                imageUris.filter { it.scheme != "http" && it.scheme != "https" }

                            // Subimos solo las nuevas
                            val newUploadedUrls = if (localUris.isNotEmpty()) {
                                val url = imageStorage.uploadItemImages(productId, localUris)
                                Log.e("image_load","New image: ${url}")
                                url
                            } else {
                                emptyList()
                            }

                            // La lista final: las que ya estaban + las nuevas subidas
                            val finalImageUrls = remoteUrls.map { it.toString() } + newUploadedUrls

                            val updatedItem = currentItem!!.copy(
                                name = title.trim(),
                                details = description.trim(),
                                status = status,
                                imageUrls = finalImageUrls
                            )

                            val result = itemManager.updateItem(updatedItem)
                            if (result.isSuccess) {
                                Toast.makeText(
                                    context,
                                    "Producto actualizado ✅",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onBack()
                            }
                            isLoadingSaveOrDelete = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                if (isLoadingSaveOrDelete) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    Text(text = stringResource(R.string.save_changes))
                }
            }

            Button(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .offset(y = (-5).dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoadingSaveOrDelete) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    Text(text = stringResource(R.string.delete_product))
                }
            }

        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(text = stringResource(R.string.delete_product)) },
            text = { Text(text = stringResource(R.string.confirm_deletion)) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        if (!isLoadingSaveOrDelete) {
                            isLoadingSaveOrDelete = true
                            val result = itemManager.deleteItem(productId)
                            if (result.isSuccess) {
                                Toast.makeText(context, "Producto eliminado ✅", Toast.LENGTH_SHORT)
                                    .show()
                                onBack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "No se puede eliminar el producto ❌",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            showDeleteConfirm = false
                        }
                        isLoadingSaveOrDelete = false
                    }
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}