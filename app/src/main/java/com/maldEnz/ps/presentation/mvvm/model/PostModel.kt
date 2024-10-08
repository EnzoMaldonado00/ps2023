package com.maldEnz.ps.presentation.mvvm.model

import java.util.TimeZone

data class PostModel(
    val postId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val timestamp: Long = 0,
    val dateTime: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val likes: List<Map<String, Any>>,
    val comments: List<Map<String, Any>>,
    val dateTimeZone: String = TimeZone.getDefault().id.toString(),
)
