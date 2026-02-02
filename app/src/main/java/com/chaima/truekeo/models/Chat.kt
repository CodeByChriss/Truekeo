package com.chaima.truekeo.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

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
    val lastTimestamp: Long get() = messages.lastOrNull()?.getLongTimestamp() ?: 0L
}

data class ChatMessage(
    val senderId: String = "",
    val text: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null,
    @get:Exclude val isFromMe : Boolean = false
){
    fun getLongTimestamp(): Long {
        return timestamp?.toDate()?.time ?: System.currentTimeMillis()
    }
}