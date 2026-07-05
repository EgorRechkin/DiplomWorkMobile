package com.example.diplomwork.platform


expect class DateTimeProvider() {
    fun currentTimeString(): String  // "HH:mm"
    fun currentDateString(): String  // "yyyy-MM-dd"
}