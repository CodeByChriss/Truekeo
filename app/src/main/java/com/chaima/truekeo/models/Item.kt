package com.chaima.truekeo.models

data class Item(
    val id: String,
    val title: String,
    val details: String? = null,
    val imageUrl: String,
    val condition: ItemCondition = ItemCondition.GOOD
)

enum class ItemCondition { NEW, LIKE_NEW, GOOD, FAIR, POOR }