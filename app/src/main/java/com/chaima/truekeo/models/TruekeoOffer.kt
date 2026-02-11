package com.chaima.truekeo.models

data class TruekeOffer(
    val id: String = "",
    val truekeId: String = "",

    val proposerUserId: String = "",
    val offeredItemId: String = "",

    val status: OfferStatus = OfferStatus.PENDING,

    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

enum class OfferStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED
}