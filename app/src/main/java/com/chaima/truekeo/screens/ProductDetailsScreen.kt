package com.chaima.truekeo.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.chaima.truekeo.R
import com.chaima.truekeo.components.BrandField
import com.chaima.truekeo.components.ImageSelectorGrid
import com.chaima.truekeo.components.ItemConditionDropdown
import com.chaima.truekeo.managers.ImageStorageManager
import com.chaima.truekeo.managers.ItemContainer
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.ui.theme.TruekeoTheme
import com.chaima.truekeo.utils.FormErrorText
import kotlinx.coroutines.launch
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val itemManager = ItemContainer.itemManager

    var isLoading by remember { mutableStateOf(true) }
    var isLoadingSaveOrDelete by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf<Item?>(null) }

    // Estado original (para comparar si hay cambios)
    var originalName by remember { mutableStateOf("") }
    var originalDescription by remember { mutableStateOf("") }
    var originalCondition by remember { mutableStateOf(ItemCondition.GOOD) }
    var originalBrand by remember { mutableStateOf("") }
    var originalImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Estado editable
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf(ItemCondition.GOOD) }
    var brand by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var currentImageSlot by remember { mutableStateOf(0) }

    var triedSubmit by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showDiscardConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        currentItem = itemManager.getItemById(productId)
        currentItem?.let {
            val uris = it.imageUrls.map { url -> url.toUri() }
            title = it.name
            description = it.details ?: ""
            condition = it.condition
            brand = it.brand ?: ""
            imageUris = uris

            originalName = it.name
            originalDescription = it.details ?: ""
            originalCondition = it.condition
            originalBrand = it.brand ?: ""
            originalImageUris = uris
        }
        isLoading = false
    }

    val hasChanges by remember {
        derivedStateOf {
            title != originalName ||
                    description != originalDescription ||
                    brand != originalBrand ||
                    condition != originalCondition ||
                    imageUris != originalImageUris
        }
    }

    val nameOk = title.trim().isNotEmpty()
    val imagesOk = imageUris.isNotEmpty()
    val brandOk = brand.isNotBlank()

    val showNameError = triedSubmit && !nameOk
    val showImagesError = triedSubmit && !imagesOk
    val showBrandError = triedSubmit && !brandOk

    val formOk = nameOk && imagesOk && brandOk

    // Interceptar el botón de atrás del sistema
    BackHandler(enabled = hasChanges) {
        showDiscardConfirm = true
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val updatedList = imageUris.toMutableList()
            if (currentImageSlot < updatedList.size) updatedList[currentImageSlot] = uri
            else updatedList.add(uri)
            imageUris = updatedList
        }
    }

    fun handleSubmit() {
        triedSubmit = true
        focusManager.clearFocus()

        if (!formOk || isLoadingSaveOrDelete) return
        isLoadingSaveOrDelete = true

        scope.launch {
            val imageStorage = ImageStorageManager(context)

            val remoteUrls = imageUris.filter { it.scheme == "http" || it.scheme == "https" }
            val localUris = imageUris.filter { it.scheme != "http" && it.scheme != "https" }

            val newUploadedUrls = if (localUris.isNotEmpty()) {
                val url = imageStorage.uploadItemImages(productId, localUris)
                Log.e("image_load", "New image: $url")
                url
            } else emptyList()

            val finalImageUrls = remoteUrls.map { it.toString() } + newUploadedUrls

            val updatedItem = currentItem!!.copy(
                name = title.trim(),
                details = description.trim().ifBlank { null },
                condition = condition,
                brand = brand.trim().ifBlank { null },
                imageUrls = finalImageUrls
            )

            val result = itemManager.updateItem(updatedItem)
            if (result.isSuccess) {
                Toast.makeText(context, "Producto actualizado ✅", Toast.LENGTH_SHORT).show()
                onBack()
            }
            isLoadingSaveOrDelete = false
        }
    }

    TruekeoTheme(dynamicColor = false) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@TruekeoTheme
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = originalName,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily(Font(R.font.saira_semibold))
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (hasChanges) showDiscardConfirm = true else onBack()
                        }) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column {
                        Spacer(Modifier.height(12.dp))

                        Column(modifier = Modifier.fillMaxWidth()) {
                            ImageSelectorGrid(
                                images = imageUris,
                                maxImages = 5,
                                onAddImage = { slot ->
                                    currentImageSlot = slot
                                    pickImageLauncher.launch("image/*")
                                },
                                onRemoveImage = { index ->
                                    imageUris = imageUris.toMutableList().apply { removeAt(index) }
                                }
                            )
                            FormErrorText(
                                showError = showImagesError,
                                message = stringResource(R.string.at_least_one_image_required)
                            )

                            Spacer(Modifier.height(7.dp))

                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text(stringResource(R.string.name)) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            FormErrorText(showError = showNameError)

                            Spacer(Modifier.height(7.dp))

                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text(stringResource(R.string.description)) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp),
                                maxLines = 5
                            )

                            Spacer(Modifier.height(7.dp))

                            ItemConditionDropdown(
                                value = condition,
                                onValueChange = { condition = it },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(7.dp))

                            BrandField(
                                value = brand,
                                onValueChange = { brand = it },
                                label = stringResource(R.string.brand),
                                modifier = Modifier.fillMaxWidth()
                            )
                            FormErrorText(
                                showError = showBrandError,
                                message = stringResource(R.string.select_valid_brand)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { handleSubmit() },
                        enabled = hasChanges && !isLoadingSaveOrDelete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoadingSaveOrDelete) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = stringResource(R.string.save_changes).uppercase(Locale.getDefault()),
                                style = MaterialTheme.typography.bodyLarge,
                                fontFamily = FontFamily(Font(R.font.saira_medium))
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { showDeleteConfirm = true },
                        enabled = !isLoadingSaveOrDelete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.delete_product).uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily(Font(R.font.saira_medium))
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        // Diálogo: confirmar eliminación
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
                                    Toast.makeText(context, "Producto eliminado ✅", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    Toast.makeText(context, "No se puede eliminar el producto ❌", Toast.LENGTH_SHORT).show()
                                }
                                showDeleteConfirm = false
                                isLoadingSaveOrDelete = false
                            }
                        }
                    }) { Text("Sí") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }

        // Diálogo: descartar cambios
        if (showDiscardConfirm) {
            AlertDialog(
                onDismissRequest = { showDiscardConfirm = false },
                title = { Text(text = stringResource(R.string.discard_changes)) },
                text = { Text(text = stringResource(R.string.discard_changes_message)) },
                confirmButton = {
                    TextButton(onClick = {
                        showDiscardConfirm = false
                        onBack()
                    }) { Text(text = stringResource(R.string.discard)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDiscardConfirm = false }) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}