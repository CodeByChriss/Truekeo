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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import coil3.compose.AsyncImage
import com.chaima.truekeo.R
import com.chaima.truekeo.data.AuthContainer
import com.chaima.truekeo.data.ChatContainer
import com.chaima.truekeo.models.ChatMessage
import com.chaima.truekeo.models.Conversation
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

    // Cargamos los datos se la conversación con los mensajes y todo
    LaunchedEffect(conversationId) {
        conversation = chatManager.getConversationById(conversationId, user?.id ?: "error")
        if(conversation == null){
            onBack() // si ha habido algún error se vuelve atrás
        }else{
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
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
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
                            containerColor = Color.White
                        )
                    )
                },
                bottomBar = {
                    Surface(
                        color = Color(0xFFF5F5F5),
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
                                    focusedContainerColor = Color(0xFFF0F0F0),
                                    unfocusedContainerColor = Color(0xFFF0F0F0),
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
                                            messageToSend
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
            ) { innerPadding ->
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Color(0xFFF5F5F5)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatMessages) { msg ->
                        ChatBubble(msg)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isFromMe) MaterialTheme.colorScheme.primary else Color.White
    val textColor = if (message.isFromMe) Color.White else Color.Black
    val shape = if (message.isFromMe) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = bgColor,
                shape = shape,
                tonalElevation = 2.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    color = textColor
                )
            }
            Text(
                text = formatTimestamp(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}