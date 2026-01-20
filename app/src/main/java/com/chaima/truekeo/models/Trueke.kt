package com.chaima.truekeo.models

import android.content.Context
import com.chaima.truekeo.R
import java.time.Instant

data class Trueke(
    val id: String,
    val name: String,
    val description: String? = null,

    val hostUser: User,
    val hostItem: Item,

    val takerUserId: String? = null,
    val takerItem: Item? = null,

    val location: GeoPoint? = null,

    val status: TruekeStatus = TruekeStatus.OPEN,
    val createdAt: Instant = Instant.now()
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