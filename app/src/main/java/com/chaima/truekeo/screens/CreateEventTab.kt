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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaima.truekeo.R
import com.chaima.truekeo.models.ItemCondition
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventTab(){
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    // Info del trueke a crear
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var locationText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd / MM / yyyy") }

    // Producto (Item)
    var itemTitle by remember { mutableStateOf("") }
    var itemDetails by remember { mutableStateOf("") }
    var itemCondition by remember { mutableStateOf(ItemCondition.GOOD) }
    var itemImageUri by remember { mutableStateOf<Uri?>(null) }
    val pickItemImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) itemImageUri = uri
    }

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
            Spacer(Modifier.height(28.dp))

            Text(
                text = "Crear trueke",
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.saira_medium)),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(22.dp))

            // Sección de información del trueke
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Título") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Detalles") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                maxLines = 6
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = selectedDate?.format(dateFormatter) ?: "",
                onValueChange = {},
                placeholder = { Text("DD / MM / YYYY") },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = locationText,
                onValueChange = { locationText = it },
                placeholder = { Text("Ubicación") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            // Sección de información del producto del trueke
            Text(
                text = "Producto del trueque",
                fontFamily = FontFamily(Font(R.font.saira_medium)),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(10.dp))

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = itemTitle,
                        onValueChange = { itemTitle = it },
                        placeholder = { Text("Nombre del producto") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = itemDetails,
                        onValueChange = { itemDetails = it },
                        placeholder = { Text("Detalles del producto") },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 5
                    )

                    Spacer(Modifier.height(12.dp))

                    // Dropdown condición
                    ItemConditionDropdown(
                        value = itemCondition,
                        onValueChange = { itemCondition = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { pickItemImageLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (itemImageUri == null) "Subir imagen del producto" else "Cambiar imagen del producto",
                            fontFamily = FontFamily(Font(R.font.saira_medium))
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "CREAR",
                    fontFamily = FontFamily(Font(R.font.saira_regular))
                )
            }

            Spacer(Modifier.height(24.dp))
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
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value.displayName(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Estado del producto") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ItemCondition.entries.forEach { condition ->
                DropdownMenuItem(
                    text = { Text(condition.displayName()) },
                    onClick = {
                        onValueChange(condition)
                        expanded = false
                    }
                )
            }
        }
    }
}