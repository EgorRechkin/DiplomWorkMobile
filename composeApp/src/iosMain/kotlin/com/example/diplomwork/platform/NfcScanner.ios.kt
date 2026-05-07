package com.example.diplomwork.platform

// iosMain/NfcScanner.ios.kt
actual class NfcScanner {
    actual fun startScan(onTagRead: (String) -> Unit) {
        // NFCNDEFReaderSession через CoreNFC
    }
    actual fun stopScan(): Unit{

    }
}