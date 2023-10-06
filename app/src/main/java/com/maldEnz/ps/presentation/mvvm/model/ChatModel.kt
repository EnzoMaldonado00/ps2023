package com.maldEnz.ps.presentation.mvvm.model

data class ChatModel(
    val chatId: String,
    val lastMessage: String?,
    val lastMessageTimestamp: Long?
)
