package com.example.diplomwork

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform