package com.chaima.truekeo.models

import android.content.Context
import com.chaima.truekeo.R

data class Item(
    val id: String = "",
    val name: String = "",
    val details: String? = null,
    val imageUrls: List<String> = emptyList(),
    val brand: String? = null,
    val condition: ItemCondition = ItemCondition.GOOD,
    val ownerId: String = "",
    val status: ItemStatus = ItemStatus.AVAILABLE
)

enum class ItemCondition {
    NEW,
    LIKE_NEW,
    GOOD,
    FAIR,
    POOR;

    fun getStringResource(): Int = when (this) {
        NEW -> R.string.product_state_new
        LIKE_NEW -> R.string.product_state_like_new
        GOOD -> R.string.product_state_good
        FAIR -> R.string.product_state_fair
        POOR -> R.string.product_state_poor
    }

    fun displayName(context: Context): String {
        return context.getString(getStringResource())
    }
}

enum class ItemStatus {
    AVAILABLE,
    RESERVED,
    EXCHANGED;
}