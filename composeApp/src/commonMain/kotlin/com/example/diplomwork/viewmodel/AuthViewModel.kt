package com.example.diplomwork.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplomwork.data.ApiClient
import com.example.diplomwork.platform.SessionStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthorized: Boolean = false,
    val employeeId: String? = null,
    val employeeName: String? = null,
    val tabelNumber: String? = null,
    val error: String? = null
)

class AuthViewModel(
    private val apiClient: ApiClient,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkSavedSession()
    }

    private fun checkSavedSession() {
        viewModelScope.launch {
            val savedId = sessionStorage.getEmployeeId()
            val savedName = sessionStorage.getName()
            val savedTabel = sessionStorage.getTabelNumber()

            if (savedId != null) {
                _state.update {
                    it.copy(
                        isAuthorized = true,
                        employeeId = savedId,
                        employeeName = savedName,
                        tabelNumber = savedTabel
                    )
                }
            }
        }
    }

    fun login(tabelNumber: String) {
        if (tabelNumber.isBlank()) {
            _state.update { it.copy(error = "Введите табельный номер") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = apiClient.login(tabelNumber.trim())
                sessionStorage.saveSession(
                    employeeId = response.employeeId,
                    name = response.name,
                    tabelNumber = response.tabelNumber
                )
                _state.update {
                    it.copy(
                        isLoading = false,
                        isAuthorized = true,
                        employeeId = response.employeeId,
                        employeeName = response.name,
                        tabelNumber = response.tabelNumber
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Сотрудник с таким табельным номером не найден"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionStorage.clearSession()
            _state.update { AuthState() }
        }
    }
}