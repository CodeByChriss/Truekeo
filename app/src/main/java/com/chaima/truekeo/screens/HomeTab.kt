package com.chaima.truekeo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.components.TruekeSheetContent
import com.chaima.truekeo.data.MockData
import com.chaima.truekeo.models.Conversation
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.Marker
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeTab(openConversation: (Conversation) -> Unit) {
    val madrid = Point.fromLngLat(-3.7038, 40.4168)
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(madrid)
            zoom(12.0)
        }
    }
    val scope = rememberCoroutineScope()

    val truekes = remember { MockData.sampleTruekes }

    var selectedTrueke by remember { mutableStateOf<Trueke?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Altura máxima del sheet
    val maxSheetHeight = 500.dp

    // Calcular altura del sheet para ajustar el padding del mapa
    val density = LocalDensity.current
    val sheetHeightPx = with(density) { maxSheetHeight.toPx() }

    val extraMarkerOffsetDp = 72.dp
    val extraMarkerOffsetPx = with(density) { extraMarkerOffsetDp.toPx() }

    // Función para centrar el marcador teniendo en cuenta el sheet
    fun centerMarker(trueke: Trueke) {
        val loc = trueke.location

        scope.launch {
            // Calcular el offset vertical para centrar el marcador en el espacio visible
            val offsetPx = (sheetHeightPx / 2) + extraMarkerOffsetPx

            mapViewportState.flyTo(
                cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(loc.lng, loc.lat))
                    .padding(
                        com.mapbox.maps.EdgeInsets(
                            0.0,
                            0.0,
                            offsetPx.toDouble(),
                            0.0
                        )
                    )
                    .zoom(14.0)
                    .build(),
                animationOptions = MapAnimationOptions.mapAnimationOptions {
                    duration(800)
                }
            )
        }
    }

    // Mapa de Mapbox con marcadores para cada trueke
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState
    ) {
        // Limitar la vista del mapa a España
        MapEffect(Unit) { mapView ->
            val spainBounds = CoordinateBounds(
                Point.fromLngLat(-9.5, 35.8),
                Point.fromLngLat(4.6, 43.9)
            )

            mapView.mapboxMap.setBounds(
                CameraBoundsOptions.Builder()
                    .bounds(spainBounds)
                    .minZoom(4.5)
                    .maxZoom(18.0)
                    .build()
            )
        }

        truekes
            .filter { it.location != null }
            .forEach { trueke ->
                val loc = trueke.location

                Marker(
                    point = Point.fromLngLat(loc.lng, loc.lat),
                    color = MaterialTheme.colorScheme.tertiary,
                    stroke = MaterialTheme.colorScheme.primary,
                    onClick = {
                        selectedTrueke = trueke
                        showSheet = true
                        centerMarker(trueke)
                        true
                    }
                )
            }
    }

    if (showSheet && selectedTrueke != null) {
        val t = selectedTrueke!!

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                showSheet = false
                selectedTrueke = null
                scope.launch {
                    mapViewportState.flyTo(
                        cameraOptions = CameraOptions.Builder()
                            .padding(com.mapbox.maps.EdgeInsets(0.0, 0.0, 0.0, 0.0))
                            .build(),
                        animationOptions = MapAnimationOptions.mapAnimationOptions {
                            duration(200)
                        }
                    )
                }
            },
            scrimColor = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxSheetHeight)
                    .wrapContentHeight()
            ) {
                TruekeSheetContent(
                    trueke = t,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp),
                    onConversationClicked = { conversationId ->
                        openConversation(conversationId)
                    }
                )
            }
        }
    }
}