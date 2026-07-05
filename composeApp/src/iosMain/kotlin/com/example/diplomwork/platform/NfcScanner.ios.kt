package com.example.diplomwork.platform

import platform.CoreNFC.NFCNDEFReaderSession

actual class NfcScanner {

    private var readerSession: NFCNDEFReaderSession? = null
    private var onTagReadCallback: ((String) -> Unit)? = null

    actual fun startScan(onTagRead: (String) -> Unit) {
        onTagReadCallback = onTagRead

        if (!NFCNDEFReaderSession.readingAvailable) {
            println("NfcScanner iOS: NFC недоступен на этом устройстве")
            return
        }

        // NFCNDEFReaderSession требует delegate
        // Полная реализация требует отдельного NSObject delegate класса
        // что невозможно реализовать напрямую в KMP без Swift interop
        println("NfcScanner iOS: требует Swift delegate — реализуется через Swift interop")
    }

    actual fun stopScan() {
        readerSession?.invalidateSession()
        readerSession = null
        onTagReadCallback = null
    }
}