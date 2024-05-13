package com.androiddevelopers.villabuluyorum.model.chat

data class MessageModel(
    var messageId: String? = null,
    var messageData: String? = null,
    var messageSender: String? = null,
    var messageReceiver: String? = null,
    var timestamp: String? = null
)