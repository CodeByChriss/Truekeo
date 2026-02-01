package com.chaima.truekeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.chaima.truekeo.utils.TimePrefix
import com.chaima.truekeo.utils.prefixedTimeAgo

@Composable
fun TruekeCard(
    trueke: Trueke,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // area interactiva completa sin márgenes
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable{ onClick() }
    ) {
        // area con los margenes visuales de la tarjeta
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 16.dp, 24.dp, 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                when (trueke.status) {
                    TruekeStatus.OPEN -> {
                        OpenTruekeLayout(
                            trueke = trueke
                        )
                    }

                    TruekeStatus.RESERVED -> {
                        ReservedTruekeLayout(
                            trueke = trueke
                        )
                    }

                    TruekeStatus.COMPLETED -> {
                        CompletedTruekeLayout(
                            trueke = trueke
                        )
                    }

                    else -> null
                }
            }
        }
    }
}


@Composable
private fun OpenTruekeLayout(
    trueke: Trueke
) {
    val context = LocalContext.current

    val timeText = remember(trueke.createdAt, trueke.updatedAt) {
        if (trueke.updatedAt != null && trueke.updatedAt.isAfter(trueke.createdAt)) {
            prefixedTimeAgo(
                context = context,
                from = trueke.updatedAt,
                prefix = TimePrefix.UPDATED
            )
        } else {
            prefixedTimeAgo(
                context = context,
                from = trueke.createdAt,
                prefix = TimePrefix.PUBLISHED
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = trueke.title,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily(Font(R.font.saira_medium)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = timeText,
            fontSize = 12.sp,
            color = Color.Black.copy(alpha = 0.6f),
            fontFamily = FontFamily(Font(R.font.saira_regular))
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = trueke.hostItem.imageUrls.first(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = trueke.hostItem.name,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = trueke.hostItem.condition.displayName(context),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = FontFamily(Font(R.font.saira_regular))
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservedTruekeLayout(
    trueke: Trueke
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Intercambio acordado con",
                fontSize = 13.sp,
                color = Color.Black.copy(alpha = 0.80f),
                fontFamily = FontFamily(Font(R.font.saira_regular))
            )

            Spacer(Modifier.width(3.dp))

            Text(
                text = "@${trueke.takerUser?.username}",
                fontSize = 16.sp,
                color = Color.Black,
                fontFamily = FontFamily(Font(R.font.saira_medium))
            )
        }

        Spacer(Modifier.height(10.dp))

        ExchangeLayout(
            leftItem = trueke.hostItem,
            rightItem = trueke.takerItem!!,
        )
    }
}


// Intercambio entre dos usuarios
@Composable
private fun ExchangeLayout(
    leftItem: Item,
    rightItem: Item
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {

        ExchangeItemBlock(
            item = leftItem,
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
            modifier = Modifier.weight(1f)
        )
    }

}

// Bloque de cada ítem en un intercambio entre dos usuarios
@Composable
private fun ExchangeItemBlock(
    item: Item,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.35f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF2F2F2))
        ) {
            AsyncImage(
                model = item.imageUrls.firstOrNull(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                color = Color.Black.copy(alpha = 0.60f),
                fontFamily = FontFamily(Font(R.font.saira_medium)),
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CompletedTruekeLayout(
    trueke: Trueke
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
                    )
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF2F2F2))
                ) {
                    AsyncImage(
                        model = trueke.takerItem?.imageUrls?.first(),
                        contentDescription = "@${trueke.takerUser?.username}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF2F2F2))
                ) {
                    AsyncImage(
                        model = trueke.hostItem.imageUrls.first(),
                        contentDescription = "@${trueke.hostUser.username}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Flecha curvada superior (hacia abajo-derecha)
            Icon(
                imageVector = Icons.Rounded.ArrowDownward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 10.dp)
                    .size(20.dp)
            )

            // Flecha curvada inferior (hacia arriba-izquierda)
            Icon(
                imageVector = Icons.Rounded.ArrowUpward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-10).dp)
                    .size(20.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(20.dp)
                    .offset(x = (-6).dp, y = (-4).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF278652)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completado",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = trueke.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Truekeado con",
                        fontSize = 13.sp,
                        color = Color.Black.copy(alpha = 0.60f),
                        fontFamily = FontFamily(Font(R.font.saira_regular))
                    )

                    Spacer(Modifier.width(3.dp))

                    Text(
                        text = "@${trueke.takerUser?.username}",
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.saira_medium))
                    )
                }
            }

            Text(
                text = "Intercambio finalizado con éxito, el ",
                fontSize = 13.sp,
                color = Color.Black.copy(alpha = 0.60f),
                fontFamily = FontFamily(Font(R.font.saira_regular))
            )
        }
    }
}
