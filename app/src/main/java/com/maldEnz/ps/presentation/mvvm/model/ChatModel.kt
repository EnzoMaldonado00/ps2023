package com.maldEnz.ps.presentation.mvvm.model

import java.util.TimeZone

data class ChatModel(
    val chatId: String,
    val lastMessage: String,
    val lastMessageDateTime: String,
    val user1: String,
    val user2: String,
    val lastMessageTimeStamp: Long,
    val dateTimeZone: String = TimeZone.getDefault().id.toString(),
)
