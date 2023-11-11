package com.maldEnz.ps.presentation.mvvm.model

data class CommentModel(
    val userName: String,
    val userImage: String,
    val commentDate: String,
    val commentContent: String,
    val commentId: String,
    val timestamp: Long,
)
