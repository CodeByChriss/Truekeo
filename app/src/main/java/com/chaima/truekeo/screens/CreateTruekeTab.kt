package com.chaima.truekeo.screens

import android.os.Build
import android.widget.Toast
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
import com.chaima.truekeo.components.ItemSelectorCard
import com.chaima.truekeo.components.LocationSearchField
import com.chaima.truekeo.data.MockData.items
import com.chaima.truekeo.data.TruekeContainer
import com.chaima.truekeo.models.GeoPoint
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.ui.theme.TruekeoTheme
import com.chaima.truekeo.utils.FormErrorText
import kotlinx.coroutines.launch
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTruekeTab(){
    val truekeManager = remember { TruekeContainer.truekeManager }
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var locationCoordinates by remember { mutableStateOf<GeoPoint?>(null) }

    var selectedItem by remember { mutableStateOf<Item?>(null) }

    // Validación de formulario
    var triedSubmit by remember { mutableStateOf(false) }

    val titleOk = title.trim().isNotEmpty()
    val locationOk = locationText.trim().isNotEmpty() && locationCoordinates != null
    val itemOk = selectedItem != null

    val showTitleError = triedSubmit && !titleOk
    val showLocationError = triedSubmit && !locationOk
    val showItemError = triedSubmit && !itemOk

    val formOk = titleOk && locationOk && itemOk

    var isLoading by remember { mutableStateOf(false) }

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
                Column {
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

                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(stringResource(R.string.title)) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 0.dp)
                        )
                        FormErrorText(showError = showTitleError)

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
                        FormErrorText(showError = showLocationError)

                        Spacer(Modifier.height(12.dp))

                        // Sección del producto del trueke
                        Text(
                            text = stringResource(R.string.product_to_trueke),
                            fontFamily = FontFamily(Font(R.font.saira_medium)),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(Modifier.height(6.dp))

                        ItemSelectorCard(
                            items = items,
                            selectedItem = selectedItem,
                            onItemSelected = { selectedItem = it }
                        )
                        FormErrorText(
                            showError = showItemError,
                            message = stringResource(R.string.required_item_error)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    enabled = !isLoading,
                    onClick = {
                        triedSubmit = true
                        focusManager.clearFocus()

                        if (!formOk) return@Button
                        if (isLoading) return@Button

                        isLoading = true

                        scope.launch {
                            val res = truekeManager.createTrueke(
                                title = title,
                                description = details,
                                location = locationCoordinates!!,  // ya validado
                                hostItemId = selectedItem!!.id     // ya validado
                            )

                            isLoading = false

                            res.onSuccess {
                                Toast.makeText(context, "Trueke creado ✅", Toast.LENGTH_SHORT).show()

                                // opcional: limpiar formulario
                                title = ""
                                details = ""
                                locationText = ""
                                locationCoordinates = null
                                selectedItem = null
                                triedSubmit = false

                                // opcional: navegar al home/detalle si tienes nav
                            }.onFailure { e ->
                                Toast.makeText(
                                    context,
                                    e.message ?: "Error creando trueke",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Text(
                            text = stringResource(R.string.create).uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily(Font(R.font.saira_medium))
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}