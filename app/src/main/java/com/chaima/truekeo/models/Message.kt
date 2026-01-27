package com.chaima.truekeo.models

data class Conversation(
    val id: String,
    val profile_name: String,
    val profile_photo: String,
    val readed: Boolean,
    val new_messages_count: Int,
    val messages: List<ChatMessage>
) {
    val lastMessage: String get() = messages.lastOrNull()?.text ?: ""
    val lastTimestamp: Long get() = messages.lastOrNull()?.timestamp ?: 0L
}

data class ChatMessage(
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isFromMe: Boolean
)