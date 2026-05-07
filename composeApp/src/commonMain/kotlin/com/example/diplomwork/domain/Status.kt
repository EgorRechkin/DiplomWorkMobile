package com.example.diplomwork.domain

data class Status(
    val id: String,
    val name: String,
    val letterCode: String?,  // буквенный код табеля: Я, О, Б и т.д.
    val digitCode: String?    // цифровой код: 01, 09 и т.д.
)