package com.example.diplomwork.platform

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef

actual class NfcScanner(private val activity: Activity) {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private var onTagReadCallback: ((String) -> Unit)? = null

    actual fun startScan(onTagRead: (employeeId: String) -> Unit) {
        onTagReadCallback = onTagRead

        if (nfcAdapter == null || !nfcAdapter.isEnabled) return

        nfcAdapter.enableReaderMode(
            activity,
            { tag -> readTag(tag) },
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK.inv(),
            null
        )
    }

    actual fun stopScan() {
        onTagReadCallback = null
        nfcAdapter?.disableReaderMode(activity)
    }

    private fun readTag(tag: Tag) {
        val ndef = Ndef.get(tag) ?: return
        try {
            ndef.connect()
            val message = ndef.ndefMessage ?: return
            val record = message.records.firstOrNull() ?: return

            // Читаем текст из записи
            val payload = record.payload
            // Первые 3 байта — служебные (статус + язык), остальное — текст
            val employeeId = String(payload, 3, payload.size - 3, Charsets.UTF_8)
                .trim()

            onTagReadCallback?.invoke(employeeId)
        } finally {
            try { ndef.close() } catch (e: Exception) { }
        }
    }
}