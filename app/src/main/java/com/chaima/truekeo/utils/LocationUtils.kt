package com.chaima.truekeo.utils

import android.content.Context
import com.chaima.truekeo.R
import com.mapbox.geojson.Point
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.result.SearchResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun resolvePlaceName(
    context: Context,
    lng: Double,
    lat: Double
): String = suspendCancellableCoroutine { cont ->

    // Crea el motor de SearchEngine
    val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
        apiType = ApiType.GEOCODING,
        settings = SearchEngineSettings()
    )

    val options = ReverseGeoOptions.Builder(Point.fromLngLat(lng, lat))
        .limit(1)
        .build()

    // Ejecuta la búsqueda
    val task = searchEngine.search(
        options,
        object : SearchCallback {
            override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
                val r = results.firstOrNull()
                val a = r?.address

                val label = buildString {
                    // Calle + número
                    a?.street?.let { street ->
                        append(street)
                        a.houseNumber?.let { hn -> append(" $hn") }
                    }

                    // Si no hay calle, usa el name/description
                    if (isEmpty()) {
                        append(r?.descriptionText ?: r?.name ?: "")
                    }

                    // CP
                    a?.postcode?.let {
                        if (isNotEmpty()) append(", ")
                        append(it)
                    }

                    // Ciudad
                    a?.place?.let {
                        if (isNotEmpty()) append(", ")
                        append(it)
                    }
                }

                cont.resume(label.trim())
            }

            override fun onError(e: Exception) {
                cont.resumeWithException(e)
            }
        }
    )

    cont.invokeOnCancellation {
        task.cancel()
    }
}