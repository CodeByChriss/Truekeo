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
    label: String = "Ubicación"
) {
    val scope = rememberCoroutineScope()

    var suggestions by remember { mutableStateOf<List<SearchSuggestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var showSuggestions by remember { mutableStateOf(false) }

    val searchEngine = remember {
        SearchEngine.createSearchEngineWithBuiltInDataProviders(
            apiType = ApiType.GEOCODING,
            settings = SearchEngineSettings()
        )
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                // Cancelar búsqueda anterior
                searchJob?.cancel()

                if (newValue.length >= 3) {
                    searchJob = scope.launch {
                        delay(300)
                        isLoading = true

                        val searchOptions = SearchOptions(
                            limit = 5,
                            countries = listOf(IsoCountryCode.SPAIN),
                            // Usar proximidad si tienes la ubicación del usuario
                        )

                        // Usar la firma correcta con SearchSuggestionsCallback
                        searchEngine.search(
                            newValue,
                            searchOptions,
                            object : SearchSuggestionsCallback {
                                override fun onSuggestions(
                                    results: List<SearchSuggestion>,
                                    responseInfo: ResponseInfo
                                ) {
                                    isLoading = false
                                    suggestions = results.take(5)
                                    showSuggestions = true
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

        // Mostrar sugerencias
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
                                // Construir dirección completa para el título
                                val fullAddressText = buildString {
                                    val street = suggestion.address?.street
                                    val houseNumber = suggestion.address?.houseNumber
                                    val postcode = suggestion.address?.postcode
                                    val city = suggestion.address?.place

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

                                    // Si no hay datos específicos, usar name o descriptionText
                                    if (isEmpty()) {
                                        append(suggestion.name)
                                    }
                                }

                                Text(
                                    text = fullAddressText,
                                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                                    maxLines = 1,
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
                                // Usar SearchSelectionCallback con una sola sugerencia
                                searchEngine.select(
                                    suggestion,
                                    object : SearchSelectionCallback {
                                        override fun onSuggestions(
                                            suggestions: List<SearchSuggestion>,
                                            responseInfo: ResponseInfo
                                        ) {
                                            // Algunas sugerencias requieren más refinamiento
                                        }

                                        override fun onResult(
                                            suggestion: SearchSuggestion,
                                            result: com.mapbox.search.result.SearchResult,
                                            responseInfo: ResponseInfo
                                        ) {
                                            val fullAddress = buildString {
                                                val street = result.address?.street
                                                val houseNumber = result.address?.houseNumber
                                                val postcode = result.address?.postcode
                                                val city = result.address?.place

                                                if (street != null) {
                                                    append(street)
                                                    if (houseNumber != null) {
                                                        append(" $houseNumber")
                                                    }
                                                }

                                                if (postcode != null) {
                                                    if (isNotEmpty()) append(", ")
                                                    append(postcode)
                                                }

                                                if (city != null) {
                                                    if (isNotEmpty()) append(", ")
                                                    append(city)
                                                }

                                                if (isEmpty()) {
                                                    result.descriptionText?.let { append(it) }
                                                }
                                            }

                                            val locationData = LocationData(
                                                name = result.name,
                                                address = fullAddress,
                                                coordinates = GeoPoint(
                                                    lng = result.coordinate.longitude(),
                                                    lat = result.coordinate.latitude()
                                                )
                                            )
                                            onLocationSelected(locationData)
                                            onValueChange(result.name)
                                            suggestions = emptyList()
                                            showSuggestions = false
                                        }

                                        override fun onResults(
                                            suggestion: SearchSuggestion,
                                            results: List<com.mapbox.search.result.SearchResult>,
                                            responseInfo: ResponseInfo
                                        ) {
                                            results.firstOrNull()?.let { result ->
                                                // Construir dirección en formato: calle número, código postal, ciudad
                                                val fullAddress = buildString {
                                                    val street = result.address?.street
                                                    val houseNumber = result.address?.houseNumber
                                                    val postcode = result.address?.postcode
                                                    val city = result.address?.place

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

                                                    // Si no hay datos, usar descriptionText
                                                    if (isEmpty()) {
                                                        result.descriptionText?.let { append(it) }
                                                    }
                                                }

                                                val locationData = LocationData(
                                                    name = result.name,
                                                    address = fullAddress,
                                                    coordinates = GeoPoint(
                                                        lng = result.coordinate.longitude(),
                                                        lat = result.coordinate.latitude()
                                                    )
                                                )
                                                onLocationSelected(locationData)
                                                onValueChange(result.name)
                                                suggestions = emptyList()
                                                showSuggestions = false
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