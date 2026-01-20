package com.chaima.truekeo.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.R
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.result.SearchSuggestion
import com.mapbox.search.SearchSuggestionsCallback
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.SearchOptions
import com.chaima.truekeo.models.GeoPoint
import com.mapbox.search.common.IsoCountryCode
import com.mapbox.geojson.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LocationData(
    val name: String,
    val address: String,
    val coordinates: GeoPoint
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onLocationSelected: (LocationData) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    userLocation: Point? = null // Añadir ubicación del usuario para proximity
) {
    val scope = rememberCoroutineScope()

    var suggestions by remember { mutableStateOf<List<SearchSuggestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var showSuggestions by remember { mutableStateOf(false) }

    val searchEngine = remember {
        SearchEngine.createSearchEngineWithBuiltInDataProviders(
            apiType = ApiType.SEARCH_BOX,
            settings = SearchEngineSettings()
        )
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                searchJob?.cancel()

                if (newValue.length >= 3) {
                    searchJob = scope.launch {
                        delay(300)
                        isLoading = true

                        val searchOptions = SearchOptions(
                            limit = 5,
                            countries = listOf(IsoCountryCode.SPAIN),
                            proximity = userLocation // Usar ubicación del usuario para resultados más relevantes
                        )

                        searchEngine.search(
                            newValue,
                            searchOptions,
                            object : SearchSuggestionsCallback {
                                override fun onSuggestions(
                                    results: List<SearchSuggestion>,
                                    responseInfo: ResponseInfo
                                ) {
                                    isLoading = false
                                    // Filtrar resultados para priorizar direcciones exactas
                                    suggestions = results
                                        .filter { suggestion ->
                                            // Priorizar resultados que tienen street address
                                            suggestion.address?.street != null ||
                                                    suggestion.name.contains(newValue, ignoreCase = true)
                                        }
                                        .take(5)
                                    showSuggestions = suggestions.isNotEmpty()
                                }

                                override fun onError(e: Exception) {
                                    isLoading = false
                                    suggestions = emptyList()
                                    showSuggestions = false
                                }
                            }
                        )
                    }
                } else {
                    suggestions = emptyList()
                    showSuggestions = false
                }
            },
            label = { Text(label) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = {
                        onValueChange("")
                        suggestions = emptyList()
                        showSuggestions = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (showSuggestions && suggestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    suggestions.forEach { suggestion ->
                        ListItem(
                            headlineContent = {
                                val fullAddressText = buildFullAddress(
                                    suggestion.address?.street,
                                    suggestion.address?.houseNumber,
                                    suggestion.address?.postcode,
                                    suggestion.address?.place,
                                    suggestion.name
                                )

                                Text(
                                    text = fullAddressText,
                                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingContent = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = Color.White
                            ),
                            modifier = Modifier.clickable {
                                // Marcar que estamos seleccionando
                                showSuggestions = false
                                suggestions = emptyList()

                                searchEngine.select(
                                    suggestion,
                                    object : SearchSelectionCallback {
                                        override fun onSuggestions(
                                            suggestionsList: List<SearchSuggestion>,
                                            responseInfo: ResponseInfo
                                        ) {
                                            // Algunas sugerencias requieren más refinamiento
                                        }

                                        override fun onResult(
                                            suggestion: SearchSuggestion,
                                            result: com.mapbox.search.result.SearchResult,
                                            responseInfo: ResponseInfo
                                        ) {
                                            handleLocationSelection(
                                                result,
                                                onLocationSelected,
                                                onValueChange
                                            )
                                            // Resetear el flag después de un pequeño delay
                                            scope.launch {
                                                delay(100)
                                            }
                                        }

                                        override fun onResults(
                                            suggestion: SearchSuggestion,
                                            results: List<com.mapbox.search.result.SearchResult>,
                                            responseInfo: ResponseInfo
                                        ) {
                                            results.firstOrNull()?.let { result ->
                                                handleLocationSelection(
                                                    result,
                                                    onLocationSelected,
                                                    onValueChange
                                                )

                                                scope.launch {
                                                    delay(100)
                                                }
                                            }
                                        }

                                        override fun onError(e: Exception) {
                                            isLoading = false
                                        }
                                    }
                                )
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}

// Función helper para construir la dirección completa
private fun buildFullAddress(
    street: String?,
    houseNumber: String?,
    postcode: String?,
    city: String?,
    fallbackName: String
): String {
    return buildString {
        // Calle y número
        if (street != null) {
            append(street)
            if (houseNumber != null) {
                append(" $houseNumber")
            }
        }

        // Código postal
        if (postcode != null) {
            if (isNotEmpty()) append(", ")
            append(postcode)
        }

        // Ciudad
        if (city != null) {
            if (isNotEmpty()) append(", ")
            append(city)
        }

        // Si no hay datos específicos, usar el nombre
        if (isEmpty()) {
            append(fallbackName)
        }
    }
}

// Función helper para manejar la selección de ubicación
private fun handleLocationSelection(
    result: com.mapbox.search.result.SearchResult,
    onLocationSelected: (LocationData) -> Unit,
    onValueChange: (String) -> Unit
) {
    val fullAddress = buildFullAddress(
        result.address?.street,
        result.address?.houseNumber,
        result.address?.postcode,
        result.address?.place,
        result.descriptionText ?: result.name
    )

    onValueChange(fullAddress)

    val locationData = LocationData(
        name = result.name,
        address = fullAddress,
        coordinates = GeoPoint(
            lng = result.coordinate.longitude(),
            lat = result.coordinate.latitude()
        )
    )

    onLocationSelected(locationData)
}