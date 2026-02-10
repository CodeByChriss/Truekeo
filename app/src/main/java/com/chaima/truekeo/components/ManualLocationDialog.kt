package com.chaima.truekeo.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ManualLocationDialog(
    onLocationSelected: (String) -> Unit
) {
    var locationText by remember { mutableStateOf("") }
    val isValid = locationText.trim().isNotBlank()

    AlertDialog(
        // ❌ NO se puede cerrar de ninguna forma
        onDismissRequest = { /* NO HACER NADA */ },

        icon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                "Selecciona tu ubicación",
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Como no has permitido el GPS, debes introducir una ubicación para continuar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = locationText,
                    onValueChange = { locationText = it },
                    label = { Text("Ubicación") },
                    placeholder = { Text("Ej: Madrid, Barcelona, Andalucía...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isValid && locationText.isNotEmpty(),
                    supportingText = {
                        if (!isValid && locationText.isNotEmpty()) {
                            Text("La ubicación es obligatoria")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onLocationSelected(locationText.trim())
                },
                enabled = isValid // ✅ solo habilitado si hay texto
            ) {
                Text("Aceptar")
            }
        }
        // ❌ SIN dismissButton
    )
}