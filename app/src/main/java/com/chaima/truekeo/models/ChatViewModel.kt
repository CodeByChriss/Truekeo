package com.chaima.truekeo.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ChatViewModel: ViewModel() {
    var selectedMessage by mutableStateOf<Message?>(null)
        private set

    fun onMessageSelected(message: Message) {
        selectedMessage = message
    }
}