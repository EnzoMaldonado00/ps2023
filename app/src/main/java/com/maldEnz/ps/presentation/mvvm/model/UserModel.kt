package com.maldEnz.ps.presentation.mvvm.model

data class UserModel(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userImage: String,
    val isTyping: Boolean,
)
