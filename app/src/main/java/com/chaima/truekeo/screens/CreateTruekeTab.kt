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
import com.chaima.truekeo.components.ItemSelectorCard
import com.chaima.truekeo.components.LocationSearchField
import com.chaima.truekeo.data.MockData.items
import com.chaima.truekeo.models.GeoPoint
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.ui.theme.TruekeoTheme
import com.chaima.truekeo.utils.FormErrorText
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTruekeTab(){
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

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