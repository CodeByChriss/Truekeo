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
import com.chaima.truekeo.models.GeoPoint
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.ItemCondition
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.components.TruekeSheetContent
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.Marker
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.style.color.colorTheme
import java.time.Instant
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeTab() {
    val madrid = Point.fromLngLat(-3.7038, 40.4168)

    //truekes de prueba
    val truekes = remember {
        listOf(
            Trueke(
                id = "t1",
                name = "Cambio PS4 por bici",
                description = "Quedamos por Sol",
                hostUserId = "u1",
                hostItem = Item(
                    id = "i1",
                    title = "PS4 Slim",
                    "hola estos son los detalles hola estos son los detalles hola estos son los detalles",
                    "",
                    brand = "Sony",
                    ItemCondition.NEW
                ),
                location = GeoPoint(-3.7038, 40.4168),
                createdAt = Instant.now().minus(13, ChronoUnit.MINUTES)
            ),
            Trueke(
                id = "t2",
                name = "Cambio monitor por teclado mecánico",
                hostUserId = "u2",
                hostItem = Item(
                    id = "i2",
                    title = "Monitor 24''",
                    null,
                    "",
                    null,
                    ItemCondition.NEW
                ),
                location = GeoPoint(-3.7123, 40.4250),
                createdAt = Instant.now().minus(2, ChronoUnit.HOURS)
            ),
            Trueke(
                id = "t3",
                name = "Cambio libros",
                hostUserId = "u3",
                hostItem = Item(
                    id = "i3",
                    title = "Pack libros DAM",
                    null,
                    "",
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

    // Mapa de Mapbox con marcadores para cada trueke
    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                center(madrid)
                zoom(12.0)
            }
        }
    ) {
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
            },
            scrimColor = Color.Transparent
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