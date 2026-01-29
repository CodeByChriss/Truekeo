package com.chaima.truekeo.models

import android.content.Context
import com.chaima.truekeo.R
import java.time.Instant

data class Trueke(
    val id: String,
    val title: String,
    val description: String? = null,

    val hostUser: User,
    val hostItem: Item,

    val takerUser: User? = null,
    val takerItem: Item? = null,

    val location: GeoPoint,

    val status: TruekeStatus = TruekeStatus.OPEN,
    val createdAt: Instant = Instant.now(),
    val editedAt: Instant? = null
)

enum class TruekeStatus {
    OPEN, RESERVED, COMPLETED, CANCELLED;
    fun getStringResource(): Int = when (this) {
        OPEN -> R.string.trueke_state_open
        RESERVED -> R.string.trueke_state_reserved
        COMPLETED -> R.string.trueke_state_completed
        CANCELLED -> R.string.trueke_state_cancelled
    }

    fun displayName(context: Context): String {
        return context.getString(getStringResource())
    }
}