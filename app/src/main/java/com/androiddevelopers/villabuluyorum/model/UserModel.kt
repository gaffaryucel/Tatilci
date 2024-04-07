package com.androiddevelopers.villabuluyorum.model

data class UserModel(
    var userId: String? = null,
    var username: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var birthDate: String? = null,
    var gender: String? = null,
    var profileImageUrl: String? = null,
    var token: String? = null,
)