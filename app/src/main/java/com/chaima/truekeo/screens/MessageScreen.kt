package com.chaima.truekeo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import coil3.compose.AsyncImage
import com.chaima.truekeo.R
import com.chaima.truekeo.managers.AuthContainer
import com.chaima.truekeo.managers.ChatContainer
import com.chaima.truekeo.managers.ItemContainer
import com.chaima.truekeo.managers.TruekeContainer
import com.chaima.truekeo.models.ChatMessage
import com.chaima.truekeo.models.Conversation
import com.chaima.truekeo.models.Item
import com.chaima.truekeo.models.MessageType
import com.chaima.truekeo.models.OfferStatus
import com.chaima.truekeo.models.Trueke
import com.chaima.truekeo.models.TruekeStatus
import com.chaima.truekeo.ui.theme.TruekeoTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(conversationId: String?, onBack: () -> Unit) {
    if (conversationId == null) { // En caso de fallar el envio del mensaje
        onBack()
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val chatManager = ChatContainer.chatManager
    val user = AuthContainer.authManager.userProfile
    var conversation: Conversation? by remember { mutableStateOf(Conversation()) }
    var chatMessages by remember { mutableStateOf(emptyList<ChatMessage>()) }
    var textState by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Cargamos los datos se la conversación con los mensajes y todo
    LaunchedEffect(conversationId) {
        conversation = chatManager.getConversationById(conversationId, user?.id ?: "error")
        if (conversation == null) {
            onBack() // si ha habido algún error se vuelve atrás
        } else {
            chatMessages = conversation!!.messages
        }
        isLoading = false
    }

    // nos quedamos escuchamos para posibles nuevos mensajes
    LaunchedEffect(conversationId) {
        chatManager.getMessagesFlow(conversationId, user?.id ?: "error")
            .collect { updatedMessages ->
                chatMessages = updatedMessages
            }
    }

    // hacemos scroll al usuario al ultimo mensaje que haya
    // y ponemos el contador de mensajes sin leer a 0
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
            chatManager.markAsRead(conversationId, user?.id ?: "error")
        }
    }

    TruekeoTheme {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = conversation!!.otherUserPhoto,
                                    contentDescription = conversation!!.otherUserName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = conversation!!.otherUserName,
                                    fontFamily = FontFamily(Font(R.font.saira_medium)),
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = getString(context, R.string.go_back)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        actions = {
                            IconButton(onClick = {
                                scope.launch { showDeleteDialog = true }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_conversation),
                                    tint = Color.Red
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.imePadding()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = textState,
                                onValueChange = { textState = it },
                                placeholder = {
                                    Text(
                                        getString(
                                            context,
                                            R.string.write_message_here
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                shape = CircleShape,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.5.dp),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(0.5.dp),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent
                                )
                            )

                            IconButton(
                                onClick = {
                                    val messageToSend = textState
                                    textState = ""
                                    scope.launch {
                                        chatManager.sendMessage(
                                            conversation!!.id,
                                            user?.id ?: "error",
                                            messageToSend,
                                            conversation!!.participants.firstOrNull {
                                                it != (user?.id ?: "error")
                                            } ?: "error"
                                        )
                                    }
                                },
                                enabled = textState.isNotBlank(),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (textState.isNotBlank()) MaterialTheme.colorScheme.primary
                                        else Color.LightGray
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = getString(context, R.string.send),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            ) { padding ->
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(chatMessages) { msg ->
                        if (msg.type == MessageType.TRUEKE) {
                            TruekeBubble(
                                msg,
                                { truekeId, truekeItemId ->
                                    scope.launch {
                                        // actualizamos el trueke (el del mapa)
                                        val truekeManager = TruekeContainer.truekeManager
                                        truekeManager.reserveTrueke(truekeId,
                                            user?.id ?: "ERROR", truekeItemId)
                                        // actualizamos la propuesta de trueke
                                        chatManager.updateOfferStatus(
                                            conversationId,
                                            msg,
                                            OfferStatus.ACCEPTED
                                        )
                                    }
                                },
                                {
                                    scope.launch {
                                        chatManager.updateOfferStatus(
                                            conversationId,
                                            msg,
                                            OfferStatus.REJECTED
                                        )
                                    }
                                },
                                {
                                    // Si el trueke (el del mapa) ya esta reservado (ha aceptado una propuesta)
                                    // y si la propuesta no está aceptada
                                    // esta se cancela
                                    scope.launch{
                                        chatManager.updateOfferStatus(
                                            conversationId,
                                            msg,
                                            OfferStatus.CANCELLED
                                        )
                                    }
                                }
                            )
                        } else {
                            ChatBubble(msg)
                        }
                    }
                }
            }
        }

        // El diálogo de eliminar conversación
        if (showDeleteDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                title = {
                    Text(
                        text = stringResource(R.string.remove_conversation),
                        fontFamily = FontFamily(Font(R.font.saira_medium))
                    )
                },
                text = {
                    Text(stringResource(R.string.remove_conversation_text))
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showDeleteDialog = false
                            scope.launch {
                                val success = chatManager.deleteConversation(conversationId)
                                if (success) onBack()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.yes_delete), color = Color.Red)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text(stringResource(R.string.no))
                    }
                }
            )
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val columnAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val bgColor = if (message.isFromMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    val textColor = if (message.isFromMe) Color.White else MaterialTheme.colorScheme.onSurface
    val shape = if (message.isFromMe) {
        RoundedCornerShape(8.dp, 8.dp, 0.dp, 8.dp)
    } else {
        RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = columnAlignment) {
            Surface(
                color = bgColor,
                shape = shape,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = textColor
                )
            }
            Text(
                text = formatTimestamp(message.getLongTimestamp()),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun TruekeBubble(
    message: ChatMessage,
    onAccept: (truekeId: String, truekeItemId: String) -> Unit,
    onReject: () -> Unit,
    onTruekeReserved: () -> Unit
) {
    val trueke = message.truekeOffer ?: return
    val itemManager = remember { ItemContainer.itemManager }
    val truekeManager = remember { TruekeContainer.truekeManager }

    var proposerItem by remember { mutableStateOf<Item?>(null) }
    var truekeData by remember { mutableStateOf<Trueke?>(null) }
    var targetItem by remember { mutableStateOf<Item?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isAcceptLoading by remember { mutableStateOf(false) }
    var isRejectLoading by remember { mutableStateOf(false) }

    LaunchedEffect(0) {
        isLoading = true
        truekeData = truekeManager.getTruekeById(trueke.truekeId)
        if(trueke.status != OfferStatus.ACCEPTED && trueke.status != OfferStatus.REJECTED && truekeData?.status == TruekeStatus.RESERVED){
            onTruekeReserved()
        }
        targetItem = itemManager.getItemById(truekeData?.hostItemId ?: "ERROR")
        proposerItem = itemManager.getItemById(trueke.offeredItemId)
        isLoading = false
    }

    // Uso un box para que cubra todo el ancho de la pantalla
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.85f) // Lo estrecho un poco para que sea más estético
                .heightIn(min = 250.dp), // debemos indicar un mínimo para evitar que la conversación se corte
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp,
            shadowElevation = 3.dp
        ) {
            if (isLoading) {
                Box(Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                }
            } else {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.trueke_proposal),
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily(Font(R.font.saira_semibold)),
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top // Alineo arriba para que nombres largos no muevan las imágenes
                    ) {
                        ProductItem(
                            imageUrl = proposerItem?.imageUrls?.firstOrNull() ?: "",
                            name = proposerItem?.name ?: stringResource(R.string.error_loading)
                        )

                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .size(32.dp)
                        )

                        ProductItem(
                            imageUrl = targetItem?.imageUrls?.firstOrNull() ?: "",
                            name = targetItem?.name ?: stringResource(R.string.error_loading)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (trueke.status == OfferStatus.PENDING) {
                        if (!message.isFromMe) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (!isAcceptLoading) {
                                            isAcceptLoading = true
                                            onAccept(truekeData?.id ?: "ERROR", proposerItem?.id ?: "ERROR")
                                            isAcceptLoading = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isAcceptLoading) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = Color.White)
                                        }
                                    } else {
                                        Text(
                                            stringResource(R.string.accept),
                                            fontFamily = FontFamily(Font(R.font.saira_medium))
                                        )
                                    }
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (!isRejectLoading) {
                                            isRejectLoading = true
                                            onReject()
                                            isRejectLoading = false
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isRejectLoading) {
                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = Color.White)
                                        }
                                    } else {
                                        Text(
                                            stringResource(R.string.reject),
                                            color = MaterialTheme.colorScheme.error,
                                            fontFamily = FontFamily(Font(R.font.saira_medium))
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.waiting_response),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.saira_regular))
                            )
                        }
                    } else {
                        val (statusLabel, statusColor) = when (trueke.status) {
                            OfferStatus.ACCEPTED -> stringResource(R.string.trueke_accepted) to Color(
                                0xFF30B677
                            )

                            OfferStatus.REJECTED -> stringResource(R.string.trueke_rejected) to Color.Red
                            OfferStatus.CANCELLED -> stringResource(R.string.trueke_cancelled) to Color.Gray
                            else -> "" to Color.Black
                        }
                        Text(
                            text = statusLabel,
                            color = statusColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily(Font(R.font.saira_bold))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(imageUrl: String, name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp) // Un poco más ancho para dar espacio al texto
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = name,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontFamily = FontFamily(Font(R.font.saira_regular)),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}