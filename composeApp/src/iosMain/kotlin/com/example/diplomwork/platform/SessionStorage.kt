package com.example.diplomwork.platform

// iosMain
actual class SessionStorage {

    private val defaults = NSUserDefaults.standardUserDefaults

    actual suspend fun saveSession(
        employeeId: String,
        name: String,
        tabelNumber: String
    ) {
        defaults.setObject(employeeId, forKey = "employee_id")
        defaults.setObject(name, forKey = "employee_name")
        defaults.setObject(tabelNumber, forKey = "tabel_number")
        defaults.synchronize()
    }

    actual suspend fun getEmployeeId(): String? {
        return defaults.stringForKey("employee_id")
    }

    actual suspend fun getName(): String? {
        return defaults.stringForKey("employee_name")
    }

    actual suspend fun getTabelNumber(): String? {
        return defaults.stringForKey("tabel_number")
    }

    actual suspend fun clearSession() {
        defaults.removeObjectForKey("employee_id")
        defaults.removeObjectForKey("employee_name")
        defaults.removeObjectForKey("tabel_number")
        defaults.synchronize()
    }
}