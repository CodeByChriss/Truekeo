package com.chaima.truekeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.User

// Carga de imagenes con la librería COIL. Su documentacion en https://coil-kt.github.io/coil/compose/
@Composable
fun ProductImage(item: Item) {
    AsyncImage(
        model = item.imageUrls.first(),
        contentDescription = item.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}

@Composable
fun UserAvatarImage(user: User, size: Dp, modifier: Modifier = Modifier) {
    AsyncImage(
        model = user.avatarUrl,
        contentDescription = "@${user.username}",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    )
}