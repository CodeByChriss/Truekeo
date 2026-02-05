package com.chaima.truekeo.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.mapbox.geojson.Point
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSelectionCallback
import com.mapbox.search.common.IsoCountryCode
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchResultType
import com.mapbox.search.result.SearchSuggestion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

data class LocationResult(
    val point: Point,
    val zoom: Double,
    val displayName: String
)

class LocationManager(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
        apiType = ApiType.SEARCH_BOX,
        settings = SearchEngineSettings()
    )

    companion object {
        private const val TAG = "LocationManager"
    }

    // ==================== GPS FUNCTIONS ====================

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) return null

        return try {
            // primero lastLocation (rápido)
            val last = fusedLocationClient.lastLocation.await()
            if (last != null) return last

            // fallback: ubicación actual (más fiable)
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error obteniendo ubicación: ${e.message}")
            null
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(Exception("No hay permisos de ubicación"))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 segundos
        ).apply {
            setMinUpdateIntervalMillis(5000L) // 5 segundos
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
                result.locations.forEach { location ->
                    trySend(location)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                context.mainLooper
            )
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // ==================== GEOCODING FUNCTIONS ====================

    /**
     * Geocodifica una ubicación ingresada por el usuario usando Mapbox Search.
     * Devuelve las coordenadas, el nivel de zoom apropiado y el nombre formateado.
     */
    suspend fun geocodeLocation(location: String): LocationResult {
        if (location.isBlank()) {
            return getDefaultSpainLocation()
        }

        return try {
            val searchResult = searchLocationWithMapbox(location)

            if (searchResult != null) {
                val zoom = determineZoomLevel(searchResult)
                val displayName = buildDisplayName(searchResult)

                Log.d(TAG, "Ubicación encontrada: $displayName (zoom: $zoom)")

                LocationResult(
                    point = Point.fromLngLat(
                        searchResult.coordinate.longitude(),
                        searchResult.coordinate.latitude()
                    ),
                    zoom = zoom,
                    displayName = displayName
                )
            } else {
                Log.w(TAG, "No se encontró la ubicación: $location")
                getDefaultSpainLocation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error geocodificando ubicación: ${e.message}")
            getDefaultSpainLocation()
        }
    }

    /**
     * Busca una ubicación usando Mapbox Search API
     */
    private suspend fun searchLocationWithMapbox(query: String): SearchResult? =
        suspendCancellableCoroutine { continuation ->
            val searchOptions = SearchOptions(
                limit = 1,
                countries = listOf(IsoCountryCode.SPAIN)
            )

            searchEngine.search(
                query,
                searchOptions,
                object : com.mapbox.search.SearchSuggestionsCallback {
                    override fun onSuggestions(
                        suggestions: List<SearchSuggestion>,
                        responseInfo: ResponseInfo
                    ) {
                        if (suggestions.isEmpty()) {
                            continuation.resume(null)
                            return
                        }

                        // Seleccionar la primera sugerencia para obtener el resultado completo
                        searchEngine.select(
                            suggestions.first(),
                            object : SearchSelectionCallback {
                                override fun onResult(
                                    suggestion: SearchSuggestion,
                                    result: SearchResult,
                                    responseInfo: ResponseInfo
                                ) {
                                    continuation.resume(result)
                                }

                                override fun onResults(
                                    suggestion: SearchSuggestion,
                                    results: List<SearchResult>,
                                    responseInfo: ResponseInfo
                                ) {
                                    continuation.resume(results.firstOrNull())
                                }

                                override fun onSuggestions(
                                    suggestions: List<SearchSuggestion>,
                                    responseInfo: ResponseInfo
                                ) {
                                    // Ignorar, necesitamos un resultado final
                                    continuation.resume(null)
                                }

                                override fun onError(e: Exception) {
                                    Log.e(TAG, "Error en select: ${e.message}")
                                    continuation.resume(null)
                                }
                            }
                        )
                    }

                    override fun onError(e: Exception) {
                        Log.e(TAG, "Error en search: ${e.message}")
                        continuation.resume(null)
                    }
                }
            )
        }

    /**
     * Determina el nivel de zoom apropiado basado en el tipo de resultado de Mapbox
     */
    private fun determineZoomLevel(result: SearchResult): Double {
        // Obtener el primer tipo de resultado de forma segura
        val resultType = result.types.firstOrNull()

        return when (resultType) {
            // Direcciones exactas (calle con número)
            SearchResultType.ADDRESS -> 15.0

            // Calles sin número específico
            SearchResultType.STREET -> 14.0

            // Barrios o distritos
            SearchResultType.NEIGHBORHOOD, SearchResultType.DISTRICT -> 13.0

            // Lugares o POIs
            SearchResultType.PLACE, SearchResultType.POI -> 14.0

            // Localidades (pueblos, ciudades pequeñas)
            SearchResultType.LOCALITY -> 12.0

            // Postales o códigos postales
            SearchResultType.POSTCODE -> 13.0

            // Regiones o áreas administrativas
            SearchResultType.REGION -> 9.0

            // Países
            SearchResultType.COUNTRY -> 6.0

            // Por defecto (ciudades, etc.)
            else -> {
                // Intentar inferir del contexto
                val address = result.address
                val place = address?.place
                when {
                    place != null && place.length < 15 -> 12.0  // Probablemente una ciudad
                    else -> 10.0
                }
            }
        }
    }

    /**
     * Construye un nombre legible para mostrar
     */
    private fun buildDisplayName(result: SearchResult): String {
        return buildString {
            val address = result.address
            val street = address?.street
            val houseNumber = address?.houseNumber
            val place = address?.place

            if (street != null) {
                append(street)
                if (houseNumber != null) {
                    append(" $houseNumber")
                }
            } else if (result.name.isNotEmpty()) {
                append(result.name)
            }

            if (place != null) {
                if (isNotEmpty()) append(", ")
                append(place)
            }

            if (isEmpty()) {
                append(result.descriptionText ?: "Ubicación")
            }
        }
    }

    /**
     * Devuelve la ubicación por defecto (España completa)
     */
    private fun getDefaultSpainLocation(): LocationResult {
        return LocationResult(
            point = Point.fromLngLat(-3.7038, 40.4168), // Madrid
            zoom = 6.0, // Vista de toda España
            displayName = "España"
        )
    }
}