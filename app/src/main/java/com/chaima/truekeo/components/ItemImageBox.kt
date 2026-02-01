package com.chaima.truekeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.chaima.truekeo.models.Item

// Contenedor de la imagen del ítem con funcionalidad para ver en pantalla completa
@Composable
fun ItemImageBox(
    item: Item,
    height: Dp
) {
    var showImageViewer by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .height(height)
        .clip(RoundedCornerShape(12.dp))
        .clickable { showImageViewer = true }
    ) {
        ProductImage(item)

        // Indicador de múltiples imágenes si las hay
        if (item.imageUrls.size > 1) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .zIndex(1f)
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Collections,
                    contentDescription = "Varias imágenes",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showImageViewer) {
        FullScreenImageViewer(
            imageUrls = item.imageUrls,
            onDismiss = { showImageViewer = false }
        )
    }
}