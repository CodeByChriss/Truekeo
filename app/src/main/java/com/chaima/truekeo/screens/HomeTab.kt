package com.chaima.truekeo.screens

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.getString
import com.chaima.truekeo.R
import com.chaima.truekeo.components.ItemSelectorDialog
import com.chaima.truekeo.components.ManualLocationDialog
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.components.TruekeSheetContent
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.managers.ChatContainer
import com.chaima.truekeo.managers.ItemContainer
import com.chaima.truekeo.managers.ItemManager
import com.chaima.truekeo.managers.LocationManager
import com.chaima.truekeo.managers.LocationPreferences
import com.chaima.truekeo.managers.TruekeContainer
import com.chaima.truekeo.models.Item
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
import kotlin.String

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@Composable
fun HomeTab(
    openConversation: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Managers y preferencias
    val locationManager = remember { LocationManager(context) }
    val locationPreferences = remember { LocationPreferences(context) }
    val truekeManager = remember { TruekeContainer.truekeManager }
    val chatManager = remember { ChatContainer.chatManager }
    val itemManager = remember { ItemContainer.itemManager }

    var truekes by remember { mutableStateOf<List<Trueke>>(emptyList()) }
    var loadingTruekes by remember { mutableStateOf(false) }

    // Estado del mapa
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(-3.7038, 40.4168)) // Madrid por defecto
            zoom(6.0) // Vista de España
        }
    }

    // Estados de UI
    var showManualLocationDialog by remember { mutableStateOf(false) }
    var selectedTrueke by remember { mutableStateOf<Trueke?>(null) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showItemDialog by remember { mutableStateOf(false) }

    var userItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var loadingUserItems by remember { mutableStateOf(false) }

    // Altura máxima del sheet
    val maxSheetHeight = 500.dp
    val density = LocalDensity.current
    val sheetHeightPx = with(density) { maxSheetHeight.toPx() }
    val extraMarkerOffsetDp = 72.dp
    val extraMarkerOffsetPx = with(density) { extraMarkerOffsetDp.toPx() }

    // Launcher para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        locationPreferences.hasAskedPermission = true

        if (granted) {
            scope.launch {
                val loc = locationManager.getCurrentLocation()
                if (loc != null) {
                    // Si el usuario permitió GPS, (opcional) dejamos de usar manual
                    locationPreferences.clearManualLocation()

                    mapViewportState.flyTo(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(loc.longitude, loc.latitude))
                            .zoom(13.0)
                            .build(),
                        MapAnimationOptions.mapAnimationOptions { duration(900) }
                    )
                } else {
                    // Permiso concedido pero no se pudo obtener ubicación
                    showManualLocationDialog = true
                }
            }
        } else {
            // Si no acepta el diálogo del sistema lo debe hacer manual
            showManualLocationDialog = true
        }
    }

    LaunchedEffect(Unit) {
        val manual = locationPreferences.getManualLocation()

        when {
            // Si hay manual guardada, centra ahí (aunque no haya permiso)
            manual != null && locationPreferences.useManualLocation -> {
                val (lat, lng, zoom) = manual
                mapViewportState.setCameraOptions(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(lng, lat))
                        .zoom(zoom)
                        .build()
                )
            }

            // Si hay permiso, intenta GPS y centra
            locationManager.hasLocationPermission() -> {
                val loc = locationManager.getCurrentLocation()
                if (loc != null) {
                    mapViewportState.setCameraOptions(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(loc.longitude, loc.latitude))
                            .zoom(13.0)
                            .build()
                    )
                }
            }

            // Sin permiso: deja España y pregunta 1 vez; si ya preguntaste, manual
            else -> {
                if (!locationPreferences.hasAskedPermission) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    showManualLocationDialog = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        loadingTruekes = true
        truekes = truekeManager.getOpenTruekesFromOthers()
        loadingTruekes = false
    }

    fun handlerPropose(
        trueke: Trueke,
        item: Item
    ) {
        scope.launch {
            val userID = AuthContainer.authManager.userProfile?.id ?: "ERROR"

            val resultId = chatManager.sendTruekeOffer(
                truekeId = trueke.id,
                myUid = userID,
                otherUid = trueke.hostUserId,
                myProduct = item,
                truekeMessage = getString(context, R.string.trueke_proposal)
            )

            // cerramos la UI
            showItemDialog = false
            showSheet = false
            selectedTrueke = null

            if(resultId != null){
                // abrimos la conversación
                openConversation(resultId)
            }else{
                // feedback al usuario
                Log.e("HomeTab", "Error proposing")
            }
        }
    }

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

    // Diálogo de ubicación manual (solo cuando se rechaza el permiso)
    if (showManualLocationDialog) {
        ManualLocationDialog(
            onLocationSelected = { query ->
                showManualLocationDialog = false

                scope.launch {
                    val result = locationManager.geocodeLocation(query)

                    locationPreferences.saveManualLocation(
                        lat = result.point.latitude(),
                        lng = result.point.longitude(),
                        zoom = result.zoom
                    )
                    locationPreferences.hasAskedPermission = true

                    mapViewportState.flyTo(
                        CameraOptions.Builder()
                            .center(result.point)
                            .zoom(result.zoom)
                            .build(),
                        MapAnimationOptions.mapAnimationOptions { duration(900) }
                    )
                }
            }
        )
    }

    // Bottom sheet para mostrar detalles del trueke
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
                    },
                    onRequestPropose = {
                        scope.launch {
                            loadingUserItems = true
                            userItems = itemManager.getMyAvailableItems()
                            loadingUserItems = false
                            showItemDialog = true
                        }
                    }
                )
            }
        }
    }

    if (showItemDialog && selectedTrueke != null) {
        ItemSelectorDialog(
            items = userItems,
            selectedItem = null,
            showConfirmButton = true,
            onDismiss = {
                showItemDialog = false
            },
            onConfirm = { item ->
                item?.let {
                    handlerPropose(
                        trueke = selectedTrueke!!,
                        item = it
                    )
                }
            }
        )
    }
}