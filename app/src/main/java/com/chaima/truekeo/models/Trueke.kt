package com.chaima.truekeo.models

import java.time.Instant

data class Trueke(
    val id: String,
    val name: String,
    val description: String? = null,

    val hostUserId: String,
    val hostItem: Item,

    val takerUserId: String? = null,
    val takerItem: Item? = null,

    val location: GeoPoint? = null,
    val dateTime: Instant? = null,

    val status: TruekeStatus = TruekeStatus.OPEN,
    val createdAt: Instant = Instant.now()
)

enum class TruekeStatus { OPEN, RESERVED, COMPLETED, CANCELLED }