package com.chaima.truekeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chaima.truekeo.utils.resolvePlaceName
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus

// Contenido del bottom sheet que muestra los detalles del trueke seleccionado
@Composable
fun TruekeSheetContent(trueke: Trueke, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        TruekeInfoSection(trueke)

        Divider()

        TruekeHostItemSection(trueke.hostItem)

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { /* abrir chat */ },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Escribir")
            }

            Button(
                onClick = { /* aceptar */ },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = trueke.status == TruekeStatus.OPEN
            ) {
                Text("Aceptar")
            }
        }
    }
}

// Sección que muestra la información general del trueke
@Composable
private fun TruekeInfoSection(trueke: Trueke) {
    val context = LocalContext.current
    val location = trueke.location

    var placeName by remember(trueke.id) { mutableStateOf<String?>(null) }

    LaunchedEffect(trueke.id) {
        location?.let {
            placeName = runCatching {
                resolvePlaceName(context, it.lng, it.lat)
            }.getOrNull()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Text(
            text = trueke.name,
            style = MaterialTheme.typography.titleLarge
        )

        if (!trueke.description.isNullOrBlank()) {
            Text(
                text = trueke.description!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        location?.let { loc ->
            Text(
                text = "Ubicación: ${placeName ?: "${loc.lat}, ${loc.lng}"}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        trueke.dateTime?.let {
            Text(
                text = "Fecha: $it",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// Sección que muestra el ítem ofrecido por el host del trueke
@Composable
private fun TruekeHostItemSection(item: Item) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Producto ofrecido",
            style = MaterialTheme.typography.titleMedium
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            KeyValueRow(label = "Nombre", value = item.title)
            Divider()

            if (!item.details.isNullOrBlank()) {
                KeyValueRow(
                    label = "Detalles",
                    value = item.details!!,
                    multiline = true
                )
                Divider()
            }

            KeyValueRow(label = "Estado", value = item.condition.displayName())
            Divider()

            KeyValueRow(label = "Marca", value = item.title)
        }
    }
}

@Composable
private fun KeyValueRow(
    label: String,
    value: String,
    multiline: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(110.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Black)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = if (multiline) Int.MAX_VALUE else 1
        )
    }
}