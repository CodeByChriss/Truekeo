package com.chaima.truekeo.components

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.utils.BrandData
import com.chaima.truekeo.utils.sanitizeBrand
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var expanded by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // altura aprox de un item (DropdownMenuItem suele ser ~48-56dp)
    val maxVisibleItems = 3
    val itemHeight = 56.dp
    val maxMenuHeight = itemHeight * maxVisibleItems

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { want ->
            // permitir abrir/cerrar manualmente si hay sugerencias
            expanded = want && suggestions.isNotEmpty()
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { raw ->
                val clean = sanitizeBrand(raw)
                onValueChange(clean)

                searchJob?.cancel()

                if (clean.length >= 2) {
                    searchJob = scope.launch {
                        delay(120)
                        val res = BrandData.search(clean, limit = 50)
                        suggestions = res
                        expanded = res.isNotEmpty()
                    }
                } else {
                    suggestions = emptyList()
                    expanded = false
                }
            },
            label = { Text(label) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = {
                        onValueChange("")
                        suggestions = emptyList()
                        expanded = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar")
                    }
                }
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .heightIn(max = maxMenuHeight) // solo 3 visibles
                    .verticalScroll(scrollState) // scroll
            ) {
                suggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(suggestion) },
                        onClick = {
                            onValueChange(BrandData.normalize(suggestion))
                            expanded = false
                            suggestions = emptyList()
                        }
                    )
                }
            }
        }
    }
}