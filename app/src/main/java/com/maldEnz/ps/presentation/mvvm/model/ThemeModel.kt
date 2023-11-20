package com.maldEnz.ps.presentation.mvvm.model

data class ThemeModel(
    val themeName: String,
    val description: String,
    val price: Long,
    val timesUnlocked: Long = 0,
    // val image: String,
)
