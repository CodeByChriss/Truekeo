package com.chaima.truekeo.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaima.truekeo.R
import com.chaima.truekeo.components.BrandField
import com.chaima.truekeo.components.ImageSelectorGrid
import com.chaima.truekeo.managers.ItemContainer
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.ui.theme.TruekeoTheme
import com.chaima.truekeo.utils.FormErrorText
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun CreateProductTab() {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val itemManager = remember { ItemContainer.itemManager }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf(ItemCondition.GOOD) }
    var brand by remember { mutableStateOf("") }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var currentImageSlot by remember { mutableStateOf(0) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val updatedList = imageUris.toMutableList()
            if (currentImageSlot < updatedList.size) {
                updatedList[currentImageSlot] = uri
            } else {
                updatedList.add(uri)
            }
            imageUris = updatedList
        }
    }

    var triedSubmit by remember { mutableStateOf(false) }

    val nameOk = name.trim().isNotEmpty()
    val imagesOk = imageUris.isNotEmpty()
    val brandOk = brand.isNotBlank()

    val showNameError = triedSubmit && !nameOk
    val showImagesError = triedSubmit && !imagesOk
    val showBrandError = triedSubmit && !brandOk

    val formOk = nameOk && imagesOk && brandOk

    var isCreating by remember { mutableStateOf(false) }

    fun resetForm() {
        name = ""
        description = ""
        condition = ItemCondition.GOOD
        imageUris = emptyList()
        triedSubmit = false
        brand = ""
    }

    fun handleSubmit() {
        triedSubmit = true
        focusManager.clearFocus()

        if (!formOk) return
        if (isCreating) return

        isCreating = true

        scope.launch {
            val res = itemManager.createItem(
                name = name,
                details = description.ifBlank { null },
                imageUris = imageUris,
                brand = brand.ifBlank { null },
                condition = condition,
                context = context
            )

            isCreating = false

            res.onSuccess { productId ->
                Toast.makeText(context, "Producto creado ✅", Toast.LENGTH_SHORT).show()

                // resetear formulario
                resetForm()
            }.onFailure { e ->
                Toast.makeText(
                    context,
                    e.message ?: "Error creando el producto",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    TruekeoTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.create_product),
                        fontSize = 32.sp,
                        fontFamily = FontFamily(Font(R.font.saira_medium)),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

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
                                imageUris = imageUris.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        )
                        FormErrorText(
                            showError = showImagesError,
                            message = stringResource(R.string.at_least_one_image_required)
                        )

                        Spacer(Modifier.height(7.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
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

                        // Dropdown condición
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.create).uppercase(Locale.getDefault()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily(Font(R.font.saira_medium))
                    )
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// Dropdown para seleccionar la condición del producto del trueke
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemConditionDropdown(
    value: ItemCondition,
    onValueChange: (ItemCondition) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value.displayName(context),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.state)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            ItemCondition.entries.forEach { condition ->
                DropdownMenuItem(
                    text = { Text(condition.displayName(context)) },
                    onClick = {
                        onValueChange(condition)
                        expanded = false
                    }
                )
            }
        }
    }
}