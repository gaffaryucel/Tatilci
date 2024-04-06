package com.androiddevelopers.villabuluyorum.model
class UserModel{
    var userId: String? = null
    var username: String? = null
    var email: String? = null
    var phoneNumber: String? = null
    var birthDate: String? = null
    var gender: String? = null
    var profileImageUrl: String? = null
    var token: String? = null

    constructor(
        userId: String? = null,
        username: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        birthDate: String? = null,
        gender: String? = null,
        profileImageUrl: String? = null,
        token: String? = null,
    ){
        this.userId = userId
        this.username =  username
        this.email =  email
        this.phoneNumber =  phoneNumber
        this.birthDate =  birthDate
        this.gender =  gender
        this.profileImageUrl =  profileImageUrl
        this.token =  token
    }
}
