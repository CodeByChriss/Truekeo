package com.chaima.truekeo.models

data class User(
    val id: String = "",
    val username: String = "",
    val firstAndLastName: String = "No name",
    val avatarUrl: String = "https://xcawesphifjagaixywdh.supabase.co/storage/v1/object/public/profile_photos/pf_default.png",
    val email: String = ""
)