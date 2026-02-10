package com.chaima.truekeo.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.components.ItemSelectorCard
import com.chaima.truekeo.components.LocationSearchField
import com.chaima.truekeo.data.ItemContainer
import com.chaima.truekeo.data.TruekeContainer
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTruekeScreen(
    truekeId: String,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val truekeManager = remember { TruekeContainer.truekeManager }
    val itemManager = remember { ItemContainer.itemManager }

    var trueke by remember { mutableStateOf<Trueke?>(null) }

    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Campos editables
    var title by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf(0.0) }
    var lng by remember { mutableStateOf(0.0) }
    var hostItemId by remember { mutableStateOf("") }

    // Copia original para detectar cambios
    var originalTitle by remember { mutableStateOf("") }
    var originalDetails by remember { mutableStateOf<String?>(null) }
    var originalLat by remember { mutableStateOf(0.0) }
    var originalLng by remember { mutableStateOf(0.0) }
    var originalHostItemId by remember { mutableStateOf("") }

    // Items del usuario y el seleccionado para el trueke
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var loadingItems by remember { mutableStateOf(false) }

    var showCancelDialog by remember { mutableStateOf(false) }

    LaunchedEffect(truekeId) {
        loading = true
        error = null
        val t = truekeManager.getTruekeById(truekeId)
        trueke = t
        loading = false

        if (t == null) {
            error = "Trueke no encontrado"
            return@LaunchedEffect
        }

        // Inicializa originales y editables
        originalTitle = t.title
        originalDetails = t.description
        originalLat = t.location.lat
        originalLng = t.location.lng
        originalHostItemId = t.hostItemId

        title = t.title
        details = t.description.orEmpty()
        lat = t.location.lat
        lng = t.location.lng
        hostItemId = t.hostItemId

        loadingItems = true
        val available = itemManager.getMyAvailableItems()

        val currentItem = itemManager.getItemById(t.hostItemId)

        items = buildList {
            currentItem?.let { add(it) }
            addAll(available.filter { it.id != currentItem?.id })
        }

        selectedItem = currentItem
        loadingItems = false

        loading = false
    }

    val hasChanges = remember(
        title, details, lat, lng, hostItemId,
        originalTitle, originalDetails, originalLat, originalLng, originalHostItemId
    ) {
        val descNorm = details.ifBlank { "" }.trim()
        val origDescNorm = (originalDetails ?: "").ifBlank { "" }.trim()

        title.trim() != originalTitle.trim() ||
                descNorm != origDescNorm ||
                lat != originalLat ||
                lng != originalLng ||
                hostItemId != originalHostItemId
    }

    val canSave = hasChanges &&
            title.trim().isNotEmpty() &&
            hostItemId.isNotBlank() &&
            !saving

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Editar trueke")
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (hasChanges) showCancelDialog = true else onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Cerrar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

            error != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(error!!)
            }

            else -> {
                val scope = rememberCoroutineScope()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Título") },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Descripción") },
                        minLines = 3
                    )

                    /*LocationSearchField(
                        value = locationText,
                        onValueChange = { locationText = it },
                        label = "Ubicación",
                        modifier = Modifier.fillMaxWidth(),
                        onLocationSelected = { loc ->
                            // texto mostrado en el input
                            locationText = loc.address.ifBlank { loc.name }

                            // coordenadas reales para guardar
                            lat = loc.coordinates.lat
                            lng = loc.coordinates.lng
                        }
                    )*/

                    if (loadingItems) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        ItemSelectorCard(
                            items = items,
                            selectedItem = selectedItem,
                            onItemSelected = { item ->
                                selectedItem = item
                                hostItemId = item.id
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo al cancelar con cambios
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Tienes cambios sin guardar") },
            text = { Text("¿Quieres guardar los cambios antes de salir?") },
            confirmButton = {
                val scope = rememberCoroutineScope()
                TextButton(onClick = {
                    showCancelDialog = false
                    /*scope.launch {
                        saving = true
                        val res = truekeManager.updateTrueke(
                            truekeId = truekeId,
                            newTitle = title,
                            newDescription = description.takeIf { it.isNotBlank() },
                            newLat = lat,
                            newLng = lng,
                            newHostItemId = hostItemId
                        )
                        saving = false
                        if (res.isSuccess) onDone()
                        else error = res.exceptionOrNull()?.message ?: "Error guardando cambios"
                    }*/
                }) { Text("Guardar") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        // descartar cambios
                        showCancelDialog = false
                        onBack()
                    }) { Text("Descartar") }

                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Seguir editando")
                    }
                }
            }
        )
    }
}