package com.chaima.truekeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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
import com.chaima.truekeo.utils.resolvePlaceName

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
                        // Información importante del trueke ya que aun no hay intercambio
                        PendingTruekeLayout(
                            trueke = trueke
                        )
                    }

                    TruekeStatus.RESERVED, TruekeStatus.COMPLETED -> {
                        // 2 items (host y taker)
                        ExchangeLayout(
                            status = trueke.status,
                            leftItem = trueke.hostItem,
                            leftUser = trueke.hostUser,
                            rightItem = trueke.takerItem!!,
                            rightUser = trueke.takerUser!!
                        )
                    }

                    else -> null
                }
            }
        }
    }
}


@Composable
private fun PendingTruekeLayout(
    trueke: Trueke
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
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
            text = trueke.title,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily(Font(R.font.saira_regular))
        )

        Spacer(Modifier.height(5.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Producto que ofrezco:",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black.copy(alpha = 0.5f),
                fontFamily = FontFamily(Font(R.font.saira_regular))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
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
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily(Font(R.font.saira_regular)),
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
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily(Font(R.font.saira_regular))
                            )
                        }
                    }
                }
            }
        }
    }
}


// Intercambio entre dos usuarios
@Composable
private fun ExchangeLayout(
    status: TruekeStatus,
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

    if (status == TruekeStatus.COMPLETED) {
        CompletedBadge(
            modifier = Modifier
                .padding(10.dp)
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

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.35f)
                .clip(shape)
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

        Row (
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

@Composable
private fun CompletedBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF1B5E20)) // verde oscuro
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = "COMPLETADO",
            color = Color.White,
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.saira_medium))
        )
    }
}