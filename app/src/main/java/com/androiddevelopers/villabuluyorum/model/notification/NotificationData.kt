package com.androiddevelopers.villabuluyorum.model.notification

import com.androiddevelopers.villabuluyorum.util.NotificationTypeForActions

data class NotificationData(
    val type : NotificationTypeForActions,
    val title: String,
    val message: String,
    val profileImage : String,
    val imageUrl : String? = null,
    val chatId : String? = null,
    val reservationId : String? = null,
    val homeId : String? = null,
)

//Burada Bildirimin içinde olucak ve FirebaseMessagingService
//içinde erişilecek bilgiler olmalı


