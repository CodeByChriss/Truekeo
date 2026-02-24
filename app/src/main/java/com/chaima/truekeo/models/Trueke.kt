package com.chaima.truekeo.models

import android.content.Context
import com.chaima.truekeo.R
import com.google.firebase.firestore.Exclude
import java.time.Instant

data class Trueke(
    val id: String = "",
    val title: String = "",
    val description: String? = null,

    // ids para firestore
    val hostUserId: String = "",
    val hostItemId: String = "",
    val takerUserId: String? = null,
    val takerItemId: String? = null,

    // no se guardan en Firestore
    @get:Exclude val hostUser: User = User(),
    @get:Exclude val hostItem: Item = Item(),
    @get:Exclude val takerUser: User? = null,
    @get:Exclude val takerItem: Item? = null,

    val location: GeoPoint = GeoPoint(),
    val status: TruekeStatus = TruekeStatus.OPEN,

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    @get:Exclude
    val createdAtInstant: Instant
        get() = Instant.ofEpochMilli(createdAt)

    @get:Exclude
    val updatedAtInstant: Instant?
        get() = updatedAt.takeIf { it > 0L && it != createdAt }
            ?.let { Instant.ofEpochMilli(it) }
}

enum class TruekeStatus {
    OPEN, RESERVED, COMPLETED, CANCELLED;
}