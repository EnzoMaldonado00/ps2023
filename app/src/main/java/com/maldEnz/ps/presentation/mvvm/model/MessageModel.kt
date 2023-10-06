package com.maldEnz.ps.presentation.mvvm.model

data class MessageModel(
    val content: String = "",
    val senderUid: String = "",
    val timestamp: String = "",
    val sorterTime: Long = 0,
    val participants: List<String> = emptyList(),
)
