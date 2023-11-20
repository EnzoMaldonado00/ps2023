package com.maldEnz.ps.presentation.mvvm.model

import java.util.TimeZone

data class CommentModel(
    val userName: String,
    val userImage: String,
    val commentDate: String,
    val commentContent: String,
    val commentId: String,
    val timestamp: Long,
    val dateZone: String = TimeZone.getDefault().id.toString(),
)
