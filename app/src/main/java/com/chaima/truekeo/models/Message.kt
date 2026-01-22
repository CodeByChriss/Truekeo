package com.chaima.truekeo.models

data class Message(
    val profile_name : String,
    val profile_photo : String,
    val last_message : String,
    val readed : Boolean,
    val new_messages_count : Int
)