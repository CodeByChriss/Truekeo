package com.chaima.truekeo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaima.truekeo.R
import com.chaima.truekeo.components.MessageCard
import com.chaima.truekeo.data.AuthContainer
import com.chaima.truekeo.data.ChatContainer
import com.chaima.truekeo.models.Conversation
import com.chaima.truekeo.ui.theme.TruekeoTheme

@Composable
fun MessagesTab(onMessageClick: (String) -> Unit) {
    val user = AuthContainer.authManager.userProfile
    val chatManager = ChatContainer.chatManager
    var conversations by remember { mutableStateOf(emptyList<Conversation>()) }

    // Para mostrar una pantalla de carga mientras se recogen las conversaciones del usuario
    var isLoading by remember { mutableStateOf(true) }

    // recogemos las conversaciones del usuario
    LaunchedEffect(Unit) {
        conversations = chatManager.getConversations(user?.id ?: "error")
        isLoading = false
    }

    TruekeoTheme(dynamicColor = false) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.my_messages),
                        fontSize = 32.sp,
                        fontFamily = FontFamily(Font(R.font.saira_medium)),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        textAlign = TextAlign.Start
                    )

                    Spacer(Modifier.height(12.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
//                        HorizontalDivider(
//                            modifier = Modifier.fillMaxWidth(),
//                            color = MaterialTheme.colorScheme.outlineVariant,
//                        )

                        if (conversations.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = stringResource(R.string.no_conversation),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily(Font(R.font.saira_medium))
                                )
                            }
                        } else {
                            conversations.forEach { conversation ->
                                MessageCard(
                                    conversation = conversation,
                                    modifier = Modifier
                                        .clickable {
                                            onMessageClick(conversation.id)
                                        }
                                        .padding(horizontal = 24.dp),
                                    currentUserId = user?.id ?: "error"
                                )

                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}