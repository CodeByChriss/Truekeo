package com.chaima.truekeo.data

import com.chaima.truekeo.models.ChatMessage
import com.chaima.truekeo.models.Conversation

object MessageMockData {
    val sampleConversations = listOf(
        Conversation(
            id = "1",
            profile_name = "Cristiano",
            profile_photo = "https://assets.realmadrid.com/is/image/realmadrid/1330603286208",
            readed = false,
            new_messages_count = 1,
            messages = listOf(
                ChatMessage("m1", "¡Hola! ¿Sigue disponible el artículo?", 1706352000000, false),
                ChatMessage("m2", "Sí, claro. ¿Te interesa?", 1706352060000, true),
                ChatMessage("m3", "Que tal?", 1706352120000, false)
            )
        ),
        Conversation(
            id = "2",
            profile_name = "Messi",
            profile_photo = "https://fcb-abj-pre.s3.amazonaws.com/img/jugadors/MESSI.jpg",
            readed = true,
            new_messages_count = 0,
            messages = listOf(
                ChatMessage("m4", "Hola, soy el mejor", 1706351000000, false),
                ChatMessage("m5", "Quien soy?", 1706351100000, false)
            )
        )
    )
}