package com.izmirsoftware.tatilci.model.chat

data class ChatModel(
    var receiverId: String? = null,
    var receiverUserName: String? = null,
    var receiverUserImage: String? = null,
    var hostId: String? = null,
    var chatLastMessage: String? = null,
    var chatLastMessageTimestamp: String? = null,
    var seen: Boolean? = null
)
