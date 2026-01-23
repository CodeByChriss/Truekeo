package com.chaima.truekeo.screens

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.chaima.truekeo.R
import com.chaima.truekeo.components.ImageSelectorGrid
import com.chaima.truekeo.components.LocationSearchField
import com.chaima.truekeo.models.GeoPoint
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.ui.theme.TruekeoTheme
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTruekeTab(){
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Info del trueke a crear
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var locationCoordinates by remember { mutableStateOf<GeoPoint?>(null) }

    // Producto (Item)
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemCondition by remember { mutableStateOf(ItemCondition.GOOD) }
    var itemImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var currentImageSlot by remember { mutableStateOf(0) }

    val pickItemImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val updatedList = itemImageUris.toMutableList()
            if (currentImageSlot < updatedList.size) {
                updatedList[currentImageSlot] = uri
            } else {
                updatedList.add(uri)
            }
            itemImageUris = updatedList
        }
    }

    // Validación de formulario
    var triedSubmit by remember { mutableStateOf(false) }

    val titleOk = title.trim().isNotEmpty()
    val locationOk = locationText.trim().isNotEmpty() && locationCoordinates != null
    val itemNameOk = itemName.trim().isNotEmpty()
    val itemImagesOk = itemImageUris.isNotEmpty()

    val showTitleError = triedSubmit && !titleOk
    val showLocationError = triedSubmit && !locationOk
    val showItemNameError = triedSubmit && !itemNameOk
    val showItemImagesError = triedSubmit && !itemImagesOk

    val formOk = titleOk && locationOk && itemNameOk && itemImagesOk

    TruekeoTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.create_trueke),
                    fontSize = 32.sp,
                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(Modifier.height(12.dp))

                // Sección de información del trueke
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                FormErrorText(showError = showTitleError, message = stringResource(R.string.required_field_error))

                Spacer(Modifier.height(7.dp))

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text(stringResource(R.string.product_details_label)) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 5
                )

                Spacer(Modifier.height(7.dp))

                LocationSearchField(
                    value = locationText,
                    onValueChange = { locationText = it },
                    onLocationSelected = { locationData ->
                        locationText = locationData.name
                        locationCoordinates = locationData.coordinates
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.location)
                )
                FormErrorText(showError = showLocationError, message = stringResource(R.string.required_field_error))

                Spacer(Modifier.height(12.dp))

                // Sección de información del producto del trueke
                Text(
                    text = stringResource(R.string.product_to_trueke),
                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                Spacer(Modifier.height(6.dp))

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f)
                    )
                ) {
                    Column(Modifier.padding(14.dp, 9.dp, 14.dp, 14.dp)) {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            label = { Text(stringResource(R.string.product_name)) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        FormErrorText(showError = showItemNameError, message = stringResource(R.string.required_field_error))

                        Spacer(Modifier.height(7.dp))

                        OutlinedTextField(
                            value = itemDescription,
                            onValueChange = { itemDescription = it },
                            label = { Text(stringResource(R.string.product_description)) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            maxLines = 5
                        )

                        Spacer(Modifier.height(7.dp))

                        // Dropdown condición
                        ItemConditionDropdown(
                            value = itemCondition,
                            onValueChange = { itemCondition = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(14.dp))

                        ImageSelectorGrid(
                            images = itemImageUris,
                            maxImages = 5,
                            onAddImage = { slot ->
                                currentImageSlot = slot
                                pickItemImageLauncher.launch("image/*")
                            },
                            onRemoveImage = { index ->
                                itemImageUris = itemImageUris.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        )
                        FormErrorText(showError = showItemImagesError, message = stringResource(R.string.at_least_one_image_required))
                    }
                }

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = {
                        triedSubmit = true
                        focusManager.clearFocus()

                        if (!formOk) return@Button

                        // TODO: Aquí ya está todo OK -> crear trueke
                    },
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
            label = { Text(stringResource(R.string.product_state)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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

// Texto de error para campos de formulario
@Composable
private fun FormErrorText(
    showError: Boolean,
    message: String,
    modifier: Modifier = Modifier
) {
    if (!showError) return

    Text(
        text = message,
        style = MaterialTheme.typography.labelSmall,
        fontFamily = FontFamily(Font(R.font.saira_regular)),
        color = MaterialTheme.colorScheme.error,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 3.dp)
    )
}