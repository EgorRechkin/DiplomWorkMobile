package com.example.diplomwork.platform

expect class NfcScanner {
    fun startScan(onTagRead: (employeeId: String) -> Unit)
    fun stopScan()
}