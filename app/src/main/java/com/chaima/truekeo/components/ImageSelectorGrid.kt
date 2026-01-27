package com.chaima.truekeo.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.chaima.truekeo.R

// Componente principal que muestra una fila horizontal de 5 slots para seleccionar las imágenes de un producto
@Composable
fun ImageSelectorGrid(
    images: List<Uri>,
    maxImages: Int,
    onAddImage: (slot: Int) -> Unit,
    onRemoveImage: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val slotSize = 130.dp

    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(maxImages) { index ->
            Box(
                modifier = Modifier.size(slotSize)
            ) {
                if (index < images.size) {
                    ImageSlot(
                        imageUri = images[index],
                        onRemove = { onRemoveImage(index) },
                        onReplace = { onAddImage(index) }
                    )
                } else {
                    EmptyImageSlot(
                        onClick = { onAddImage(index) }
                    )
                }

                if (index == 0) {
                    PrimaryPhotoBadge(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(6.dp)
                            .zIndex(2f)
                    )
                }
            }
        }
    }
}

// Slot que muestra una imagen seleccionada que permite eliminarla o reemplazarla
@Composable
private fun ImageSlot(
    imageUri: Uri,
    onRemove: () -> Unit,
    onReplace: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onReplace() }
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = stringResource(R.string.product_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Botón para eliminar
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .zIndex(1f)
                .size(28.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.remove_image),
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Slot vacío para añadir una nueva imagen
@Composable
private fun EmptyImageSlot(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = stringResource(R.string.add_image),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp)
        )
    }
}

// Etiqueta de "Foto principal" para el primer slot de imagenes
@Composable
private fun PrimaryPhotoBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.20f))
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.main_product_image),
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}