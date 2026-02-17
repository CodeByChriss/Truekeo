package com.chaima.truekeo.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class Conversation(
    val id: String = "",
    val participants: List<String> = emptyList(),
    var last_message: String = "",
    var unread_count: Map<String, Int> = emptyMap(),

    // Estos campos no se guardarán en la base de datos
    // son unicamente para usarlos en la aplicación
    @get:Exclude var messages: List<ChatMessage> = emptyList(),
    @get:Exclude var otherUserName: String = "",
    @get:Exclude var otherUserPhoto: String = ""
)

data class ChatMessage(
    val senderId: String = "",
    val text: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null,
    @get:Exclude val isFromMe : Boolean = false,
    
    // Para diferenciar entre propuesta y mensaje
    val type: MessageType = MessageType.TEXT,
    val truekeOffer: TruekeOffer? = null
){
    fun getLongTimestamp(): Long {
        return timestamp?.toDate()?.time ?: System.currentTimeMillis()
    }
}

enum class MessageType{
    TEXT,
    TRUEKE
}