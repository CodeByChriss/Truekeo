package com.chaima.truekeo.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.chaima.truekeo.R
import com.chaima.truekeo.components.ItemImageBox
import com.chaima.truekeo.components.UserAvatarImage
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.managers.ChatContainer
import com.chaima.truekeo.managers.TruekeContainer
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import com.chaima.truekeo.ui.theme.TruekeoTheme
import com.chaima.truekeo.utils.completedOn
import com.chaima.truekeo.utils.resolvePlaceName
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun TruekeDetailsScreen(
    truekeId: String,
    onBack: () -> Unit,
    onOpenConversation: (String) -> Unit
) {
    val truekeManager = remember { TruekeContainer.truekeManager }
    val authManager = remember { AuthContainer.authManager }

    val currentUserId = authManager.userProfile?.id

    var trueke by remember { mutableStateOf<Trueke?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(truekeId) {
        trueke = truekeManager.getTruekeById(truekeId)
        loading = false
    }

    when {
        loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        trueke == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("Trueke no encontrado")
            }
        }

        else -> {
            val isHost = currentUserId != null && trueke!!.hostUserId == currentUserId

            TruekeDetailsContent(
                trueke = trueke!!,
                isHost = isHost,
                onBack = onBack,
                onOpenConversation = onOpenConversation,
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TruekeDetailsContent(
    trueke: Trueke,
    isHost: Boolean,
    onBack: () -> Unit,
    onOpenConversation: (String) -> Unit,
) {
    TruekeoTheme(dynamicColor = false) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = trueke.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily(Font(R.font.saira_semibold))
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            },
            containerColor = Color.White,
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            when (trueke.status) {
                TruekeStatus.OPEN ->
                    OpenTruekeLayout(trueke, Modifier.padding(padding))

                TruekeStatus.RESERVED ->
                    ReservedTruekeLayout(
                        trueke,
                        isHost,
                        onOpenConversation,
                        Modifier.padding(padding)
                    )

                TruekeStatus.COMPLETED ->
                    CompletedTruekeLayout(
                        trueke,
                        isHost,
                        onOpenConversation,
                        Modifier.padding(padding)
                    )

                TruekeStatus.CANCELLED -> {}
            }
        }
    }
}

@Composable
fun OpenTruekeLayout(
    trueke: Trueke,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Spacer(Modifier.height(12.dp))

            BasicTruekeInfo(trueke = trueke)

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.your_offer).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily(Font(R.font.saira_medium))
            )

            Spacer(Modifier.height(4.dp))

            ItemCard(item = trueke.hostItem)
        }

        Column {
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    // TODO: Aquí ya está todo OK -> crear trueke
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.edit_trueke).uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily(Font(R.font.saira_medium))
                )
            }
        }
    }
}

@Composable
fun ReservedTruekeLayout(
    trueke: Trueke,
    isHost: Boolean,
    onOpenConversation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user = AuthContainer.authManager.userProfile
    val chatManager = ChatContainer.chatManager
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    val takerItem = requireNotNull(trueke.takerItem) { "takerItem debe existir en RESERVED" }

    val myOfferItem = if (isHost) trueke.hostItem else takerItem
    val myReceiveItem = if (isHost) takerItem else trueke.hostItem

    val otherUser = if (isHost) trueke.takerUser else trueke.hostUser

    fun handleChatClick() {
        if (isLoading) return
        val myId = user?.id ?: return
        val otherId = otherUser?.id ?: return

        if (myId == otherId) {
            Toast.makeText(context, getString(context, R.string.cant_start_conversation_with_you), Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            isLoading = true
            val conversationId = chatManager.startOrGetConversation(myId, otherId)
            if (conversationId == null) {
                Toast.makeText(context, getString(context, R.string.error_starting_conversation), Toast.LENGTH_SHORT).show()
            } else {
                onOpenConversation(conversationId)
            }
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        BasicTruekeInfo(trueke = trueke)

        Spacer(Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.exchange).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily(Font(R.font.saira_medium))
        )

        Spacer(Modifier.height(2.dp))

        otherUser?.let { u ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.trade_with),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily(Font(R.font.saira_regular))
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    UserAvatarImage(u, size = 24.dp)

                    Text(
                        text = "@${u.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        fontFamily = FontFamily(Font(R.font.saira_medium))
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${stringResource(R.string.your_offer)}:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily(Font(R.font.saira_medium))
        )
        Spacer(Modifier.height(4.dp))

        ItemCard(item = myOfferItem)

        Spacer(Modifier.height(16.dp))

        // Icono de intercambio
        Icon(
            imageVector = Icons.Rounded.SwapVert,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "${stringResource(R.string.you_receive)}:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily(Font(R.font.saira_medium))
        )
        Spacer(Modifier.height(4.dp))

        ItemCard(item = myReceiveItem)

        Spacer(Modifier.height(24.dp))

        // Botones de acción
        if (isHost) {
            Button(
                onClick = { /* TODO: Marcar como completado */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.mark_as_completed).uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily(Font(R.font.saira_medium))
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* TODO: Cancelar trueke */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.cancel_trueke).uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily(Font(R.font.saira_medium))
                )
            }

            Spacer(Modifier.height(12.dp))
        }

        // Siempre sale el boton de escribir (host o no host)
        OutlinedButton(
            onClick = { handleChatClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.to_chat).uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.saira_medium))
            )
        }
    }
}

@Composable
fun CompletedTruekeLayout(
    trueke: Trueke,
    isHost: Boolean,
    onOpenConversation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user = AuthContainer.authManager.userProfile
    val chatManager = ChatContainer.chatManager
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    val completedInstant = trueke.updatedAtInstant ?: trueke.createdAtInstant

    val takerItem = requireNotNull(trueke.takerItem) { "takerItem debe existir en RESERVED" }

    val myOfferItem = if (isHost) trueke.hostItem else takerItem
    val myReceiveItem = if (isHost) takerItem else trueke.hostItem

    val otherUser = if (isHost) trueke.takerUser else trueke.hostUser

    fun handleChatClick() {
        if (isLoading) return
        val myId = user?.id ?: return
        val otherId = otherUser?.id ?: return

        if (myId == otherId) {
            Toast.makeText(context, getString(context, R.string.cant_start_conversation_with_you), Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch {
            isLoading = true
            val conversationId = chatManager.startOrGetConversation(myId, otherId)
            if (conversationId == null) {
                Toast.makeText(context, getString(context, R.string.error_starting_conversation), Toast.LENGTH_SHORT).show()
            } else {
                onOpenConversation(conversationId)
            }
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        CompletedBanner()

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier
            .padding(horizontal = 24.dp)
        ) {
            Text(
                text = completedOn(context, completedInstant),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.saira_regular))
            )

            Spacer(Modifier.height(12.dp))

            BasicTruekeInfo(trueke = trueke)

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.exchange).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily(Font(R.font.saira_medium))
            )

            Spacer(Modifier.height(2.dp))

            otherUser?.let { u ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.trade_with),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily(Font(R.font.saira_regular))
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        UserAvatarImage(u, size = 24.dp)

                        Text(
                            text = "@${u.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black,
                            fontFamily = FontFamily(Font(R.font.saira_medium))
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${stringResource(R.string.your_offer)}:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily(Font(R.font.saira_medium))
            )
            Spacer(Modifier.height(4.dp))

            ItemCard(item = myOfferItem)

            Spacer(Modifier.height(16.dp))

            // Icono de intercambio
            Icon(
                imageVector = Icons.Rounded.SwapVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${stringResource(R.string.you_receive)}:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily(Font(R.font.saira_medium))
            )
            Spacer(Modifier.height(4.dp))

            ItemCard(item = myReceiveItem)

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { handleChatClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.to_chat).uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily(Font(R.font.saira_medium))
                )
            }
        }
    }
}

@Composable
private fun BasicTruekeInfo(
    trueke: Trueke
) {
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

    trueke.description?.let {
        LabeledValue(
            label = stringResource(R.string.description),
            value = it
        )
        Spacer(Modifier.height(12.dp))
    }

    LabeledValue(
        label = stringResource(R.string.location),
        value = placeText
    )
}

@Composable
fun LabeledValue(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily(Font(R.font.saira_medium))
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily(Font(R.font.saira_regular))
        )
    }
}

@Composable
fun ItemCard(
    item: Item,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            ItemImageBox(
                item = item,
                height = 200.dp
            )

            Spacer(Modifier.height(12.dp))

            // Nombre del item
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily(Font(R.font.saira_medium))
            )

            // Detalles del item si existe
            item.details?.let { details ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily(Font(R.font.saira_regular)),
                )
            }

            // Condición del item
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = item.condition.displayName(context),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.saira_regular))
                )
            }
        }
    }
}

@Composable
fun CompletedBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF30B677)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.trade_completed).uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.saira_medium))
            )
        }
    }
}