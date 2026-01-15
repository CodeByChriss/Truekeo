package com.chaima.truekeo.models

data class User(
    val id: String,
    val username: String,
    val avatarUrl: String? = null
)