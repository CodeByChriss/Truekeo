package com.chaima.truekeo.data

import com.chaima.truekeo.models.Message

object MessageMockData {
    val sampleMessages = listOf(
        Message(
            profile_name = "Cristiano",
            profile_photo = "https://assets.realmadrid.com/is/image/realmadrid/1330603286208",
            last_message = "Que tal?",
            readed = false,
            new_messages_count = 1
        ),
        Message(
            profile_name = "Messi",
            profile_photo = "https://fcb-abj-pre.s3.amazonaws.com/img/jugadors/MESSI.jpg",
            last_message = "Quien soy?",
            readed = true,
            new_messages_count = 0
        ),
        Message(
            profile_name = "Messi v2",
            profile_photo = "https://fcb-abj-pre.s3.amazonaws.com/img/jugadors/MESSI.jpg",
            last_message = "Esto es un mensaje largo para que no lo puedas leer y te quedes con la intriga jeje SIU :D",
            readed = false,
            new_messages_count = 3
        )
    )
}