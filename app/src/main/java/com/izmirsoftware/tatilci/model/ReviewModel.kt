package com.izmirsoftware.tatilci.model

data class ReviewModel(
    var reviewId: String? = null,
    var rating: Int? = null,
    var comment: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var userProfilePictureUrl: String? = null,
    var reservationId: String? = null,
    var villaId: String? = null,
    var hostId: String? = null,
    var time: String? = null
)

