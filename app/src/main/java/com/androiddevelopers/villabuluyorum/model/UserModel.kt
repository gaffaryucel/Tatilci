package com.androiddevelopers.villabuluyorum.model

import com.androiddevelopers.villabuluyorum.util.UserType

data class UserModel(
    var userId: String? = null,
    var username: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var birthDate: String? = null,
    var gender: String? = null,
    var profileImageUrl: String? = null,
    var profileBannerUrl: String? = null,
    var token: String? = null,
    var userRating: Double? = null,
    var host: Host? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var address: UserAddress? = null,
    var userType: UserType? = null,
)

data class Host(
    var role: String? = null,
    var hostRating: Double? = null,
    var experience: Int? = null,
    var languages: List<String>? = null,
    var description: String? = null,
)

data class UserAddress(
    var province: String? = null,
    var district: String? = null,
    var neighborhood: String? = null,
    var address: String? = null
)