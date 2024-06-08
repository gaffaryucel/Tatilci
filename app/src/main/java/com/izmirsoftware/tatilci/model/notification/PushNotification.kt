package com.izmirsoftware.tatilci.model.notification


data class PushNotification(
    val data: NotificationData,
    val to: String
)