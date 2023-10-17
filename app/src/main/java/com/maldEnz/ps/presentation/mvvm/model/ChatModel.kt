package com.maldEnz.ps.presentation.mvvm.model

data class ChatModel(
    val chatId: String,
    val lastMessage: String,
    val lastMessageDateTime: String,
    val user1: String,
    val user2: String,
)
