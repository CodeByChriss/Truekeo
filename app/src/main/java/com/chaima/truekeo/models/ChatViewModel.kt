package com.chaima.truekeo.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ChatViewModel: ViewModel() {
    var selectedMessage by mutableStateOf<Conversation?>(null)
        private set

    fun onMessageSelected(conversation: Conversation) {
        selectedMessage = conversation
    }
}