package com.example.diplomwork.platform

expect class SessionStorage {
    suspend fun saveSession(employeeId: String, name: String, tabelNumber: String)
    suspend fun getEmployeeId(): String?
    suspend fun getName(): String?
    suspend fun getTabelNumber(): String?
    suspend fun clearSession()
}