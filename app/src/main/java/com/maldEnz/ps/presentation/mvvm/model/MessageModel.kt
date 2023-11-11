package com.maldEnz.ps.presentation.mvvm.model

data class MessageModel(
    val messageId: String = "",
    val conversationId: String = "",
    val content: String = "",
    val senderUid: String = "",
    val timestamp: String = "",
    val sorterTime: Long = 0,
    val participants: List<String> = emptyList(),
    val deleted: Boolean = false,
    val imageUrl: String? = null
)
