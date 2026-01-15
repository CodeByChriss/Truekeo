package com.chaima.truekeo.models

data class Item(
    val id: String,
    val title: String,
    val details: String? = null,
    val imageUrl: String,
    val brand: String? = null,
    val condition: ItemCondition = ItemCondition.GOOD
)

enum class ItemCondition {
     NEW, LIKE_NEW, GOOD, FAIR, POOR;

    fun displayName(): String = when (this) {
        NEW -> "Nuevo"
        LIKE_NEW -> "Casi nuevo"
        GOOD -> "Muy bueno"
        FAIR -> "Bueno"
        POOR -> "Satisfactorio"
    }

}