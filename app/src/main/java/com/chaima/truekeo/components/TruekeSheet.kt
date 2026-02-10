package com.chaima.truekeo.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.chaima.truekeo.R
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.managers.ChatContainer
import com.chaima.truekeo.utils.resolvePlaceName
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import com.chaima.truekeo.models.User
import com.chaima.truekeo.utils.TimePrefix
import com.chaima.truekeo.utils.prefixedTimeAgo
import kotlinx.coroutines.launch

// Contenido del bottom sheet que muestra los detalles del trueke seleccionado
@Composable
fun TruekeSheetContent(
    trueke: Trueke,
    modifier: Modifier = Modifier,
    onConversationClicked: (String) -> Unit,
    onProposeClicked: (truekeId: String) -> Unit
) {
    val scrollState = rememberScrollState()
    val user = AuthContainer.authManager.userProfile
    val chatManager = ChatContainer.chatManager
    // Para evitar realizar más de una vez la misma llamada
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    fun handleChatClick() {
        if (!isLoading) {
            val hostUserId = trueke.hostUser.id
            if (user?.id != hostUserId) {
                scope.launch {
                    isLoading = true
                    val conversationId = chatManager.startOrGetConversation(
                        user?.id ?: "error",
                        hostUserId
                    )
                    if(conversationId == null){
                        Toast.makeText(context, getString(context,R.string.error_starting_conversation), Toast.LENGTH_SHORT).show()
                    }else{
                        onConversationClicked(conversationId)
                    }
                    isLoading = false
                }
            }else{
                Toast.makeText(context, getString(context,R.string.cant_start_conversation_with_you), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleProposeClick() {
        onProposeClicked(trueke.id)
    }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        TruekeInfoSection(trueke)

        Divider()

        TruekeHostItemSection(trueke.hostItem)

        Divider()

        UploadedByRow(trueke.hostUser)

        Spacer(modifier = Modifier.height(0.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { handleChatClick() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    Text(
                        text = stringResource(R.string.write),
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily(Font(R.font.saira_medium))
                    )
                }
            }

            Button(
                onClick = { handleProposeClick() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = trueke.status == TruekeStatus.OPEN
            ) {
                Text(
                    text = stringResource(R.string.propose),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily(Font(R.font.saira_medium))
                )
            }
        }
    }
}

// Sección que muestra la información general del trueke
@Composable
private fun TruekeInfoSection(trueke: Trueke) {
    val context = LocalContext.current
    val loc = trueke.location

    var placeText by remember(trueke.id) {
        mutableStateOf("${loc.lat}, ${loc.lng}")
    }

    LaunchedEffect(trueke.id) {
        placeText = runCatching {
            resolvePlaceName(context, loc.lng, loc.lat)
        }.getOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: "${loc.lat}, ${loc.lng}"
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = trueke.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily(Font(R.font.saira_medium)),
        )

        if (!trueke.description.isNullOrBlank()) {
            Text(
                text = trueke.description,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily(Font(R.font.saira_regular)),
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "Ubicación",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = (-3).dp)
            )

            Text(
                text = placeText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontFamily = FontFamily(Font(R.font.saira_regular)),
            )
        }


        Text(
            text = prefixedTimeAgo(
                context = context,
                from = trueke.createdAtInstant,
                prefix = TimePrefix.PUBLISHED
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily(Font(R.font.saira_regular)),
        )
    }
}

// Sección que muestra el ítem ofrecido por el host del trueke
@Composable
private fun TruekeHostItemSection(item: Item) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.product_offered),
            style = MaterialTheme.typography.titleMedium,
            fontFamily = FontFamily(Font(R.font.saira_medium)),
        )

        Column {
            KeyValueRow(label = stringResource(R.string.product_name_label), value = item.name)
            RowDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (!item.details.isNullOrBlank()) {
                KeyValueRow(
                    label = stringResource(R.string.product_details_label),
                    value = item.details,
                    multiline = true
                )
                RowDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (!item.brand.isNullOrBlank()) {
                KeyValueRow(
                    label = stringResource(R.string.product_brand_label),
                    value = item.brand,
                    multiline = true
                )
                RowDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            KeyValueRow(
                label = stringResource(R.string.product_status_label),
                value = item.condition.displayName(context)
            )
        }
    }
    // Imagen del ítem
    ItemImageBox(item, 180.dp)
}

@Composable
private fun KeyValueRow(
    label: String,
    value: String,
    multiline: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily(Font(R.font.saira_regular)),
            modifier = Modifier
                .width(80.dp)
                .padding(0.dp, 8.dp, 8.dp, 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(0.5.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily(Font(R.font.saira_medium)),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            maxLines = if (multiline) Int.MAX_VALUE else 1
        )
    }
}

@Composable
private fun RowDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 0.5.dp,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

@Composable
private fun UploadedByRow(hostUser: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.uploaded_by),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily(Font(R.font.saira_regular)),
        )

        Spacer(Modifier.width(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            UserAvatarImage(hostUser, size = 20.dp)

            Text(
                text = "@${hostUser.username}",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily(Font(R.font.saira_medium)),
                maxLines = 1
            )
        }
    }
}