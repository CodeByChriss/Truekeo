package com.chaima.truekeo.models

data class User(
    val id: String = "",
    val username: String = "",
    val firstAndLastName: String = "No name",
    val avatarUrl: String? = null,
    val email: String = ""
)