package com.chaima.truekeo.data.models

import com.chaima.truekeo.models.GeoPoint

data class TruekeDto(
    val id: String = "",
    val title: String = "",
    val description: String? = null,

    val hostUserId: String = "",
    val hostItemId: String = "",

    val takerUserId: String? = null,
    val takerItemId: String? = null,

    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val status: String = "OPEN",

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)