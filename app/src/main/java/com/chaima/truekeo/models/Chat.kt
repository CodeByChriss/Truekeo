package com.chaima.truekeo.models

import com.google.firebase.firestore.Exclude

data class Conversation(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val readed: Boolean = false,
    var last_message: String = "",
    var new_messages_count: Int = 0,

    // Estos campos no se guardarán en la base de datos
    // son unicamente para usarlos en la aplicación
    @get:Exclude var messages: List<ChatMessage> = emptyList(),
    @get:Exclude var otherUserName: String = "",
    @get:Exclude var otherUserPhoto: String = ""
) {
    @get:Exclude
    val lastTimestamp: Long get() = messages.lastOrNull()?.timestamp ?: 0L
}

data class ChatMessage(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L,
    @get:Exclude val isFromMe : Boolean = false
)