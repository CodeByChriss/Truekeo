package com.chaima.truekeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.chaima.truekeo.R
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import com.chaima.truekeo.models.User
import com.chaima.truekeo.ui.theme.TruekeoTheme

@Composable
fun TruekeCard(
    trueke: Trueke,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val (chipBg, chipFg) = statusColors(trueke.status)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp) // Espacio para la pestaña superior
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            ExchangeLayout(
                leftItem = trueke.hostItem,
                leftUser = trueke.hostUser,
                rightItem = trueke.takerItem!!,
                rightUser = trueke.takerUser!!
            )
        }

        // Pestaña que cuelga del borde superior
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 8.dp
                    )
                )
                .background(chipBg)
                .padding(horizontal = 16.dp, vertical = 2.dp)

        ) {
            Text(
                text = trueke.status.displayName(context),
                color = chipFg,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.saira_bold))
            )
        }
    }
}

// Intercambio entre dos usuarios
@Composable
private fun ExchangeLayout(
    leftItem: Item,
    leftUser: User,
    rightItem: Item,
    rightUser: User
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ExchangeItemBlock(
            item = leftItem,
            user = leftUser,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 3.dp)
                .size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                tint = Color.Black
            )
        }

        ExchangeItemBlock(
            item = rightItem,
            user = rightUser,
            modifier = Modifier.weight(1f)
        )
    }
}

// Bloque de cada ítem en un intercambio entre dos usuarios
@Composable
private fun ExchangeItemBlock(
    item: Item,
    user: User,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    TruekeoTheme {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.35f)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = item.name,
                fontSize = 14.sp,
                lineHeight = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily(Font(R.font.saira_medium)),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.uploaded_by) + " ",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily(Font(R.font.saira_regular)),
                    maxLines = 1
                )

                Text(
                    text = "@${user.username}",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                    maxLines = 1
                )
            }
        }
    }
}

private fun statusColors(status: TruekeStatus): Pair<Color, Color> {
    return when (status) {
        TruekeStatus.OPEN -> Color(0xFFFFF2A6) to Color(0xFF8A6A00)
        TruekeStatus.RESERVED -> Color(0xFFA7F3A0) to Color(0xFF1E6B1E)
        TruekeStatus.COMPLETED -> Color(0xFFBFE6FF) to Color(0xFF0B4A6D)
        TruekeStatus.CANCELLED -> Color(0xFFFFA0A0) to Color(0xFF7A1010)
    }
}