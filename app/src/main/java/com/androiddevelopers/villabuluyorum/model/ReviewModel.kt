package com.androiddevelopers.villabuluyorum.model

data class ReviewModel(
    var reviewId: String? = null,
    var rating: Float? = null,
    var comment: String? = null,
    var userId: String ? = null,
    var userName: String ? = null,
    var userProfilePictureUrl: String? = null,
    var reservationId: String? = null,
    var hostId: String? = null,
    var time : String? = null
)

