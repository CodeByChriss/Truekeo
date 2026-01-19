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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import com.chaima.truekeo.models.GeoPoint
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.components.TruekeSheetContent
import com.chaima.truekeo.models.User
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
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeTab() {
    val madrid = Point.fromLngLat(-3.7038, 40.4168)
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(madrid)
            zoom(12.0)
        }
    }
    val scope = rememberCoroutineScope()

    //usuario de prueba
    val user = User(
        id = "u1",
        username = "Chaima",
        avatarUrl = "https://img.freepik.com/foto-gratis/hombre-negro-posando_23-2148171639.jpg?semt=ais_hybrid&w=740&q=80",
    )

    //truekes de prueba
    val truekes = remember {
        listOf(
            Trueke(
                id = "t1",
                name = "Cambio PS4 por bici",
                description = "Quedamos por Sol",
                hostUser = user,
                hostItem = Item(
                    id = "i1",
                    title = "PS4 Slim",
                    "hola estos son los detalles hola estos son los detalles hola estos son los detalles hola estos son los detalles hola estos son los detalles hola estos son los detalles",
                    "https://cdn.wallapop.com/images/10420/k5/qk/__/c10420p1218961628/i6217265588.jpg?pictureSize=W320",
                    brand = "Sony",
                    ItemCondition.NEW
                ),
                location = GeoPoint(-3.7038, 40.4168),
                createdAt = Instant.now().minus(13, ChronoUnit.MINUTES)
            ),
            Trueke(
                id = "t2",
                name = "Cambio monitor por teclado mecánico",
                hostUser = user,
                hostItem = Item(
                    id = "i2",
                    title = "Monitor 24''",
                    null,
                    "https://cdn.wallapop.com/images/10420/ee/jl/__/c10420p870955066/i3115083731.jpg?pictureSize=W640",
                    null,
                    ItemCondition.NEW
                ),
                location = GeoPoint(-3.7123, 40.4250),
                createdAt = Instant.now().minus(2, ChronoUnit.HOURS)
            ),
            Trueke(
                id = "t3",
                name = "Cambio libros",
                hostUser = user,
                hostItem = Item(
                    id = "i3",
                    title = "Pack libros DAM",
                    null,
                    "https://cdn.wallapop.com/images/10420/k5/ti/__/c10420p1219098489/i6218005841.jpg?pictureSize=W320",
                    "Anaya",
                    ItemCondition.NEW
                ),
                location = GeoPoint(-3.6890, 40.4095),
                createdAt = Instant.now().minus(45, ChronoUnit.DAYS)
            )
        )
    }

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
        val loc = trueke.location ?: return

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
                val loc = trueke.location!!

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
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}