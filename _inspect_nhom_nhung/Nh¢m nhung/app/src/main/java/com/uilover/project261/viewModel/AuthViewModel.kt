package com.uilover.project261.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uilover.project261.Repository.AuthRepository
import com.uilover.project261.domain.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            if (repository.isLoggedIn()) {
                _currentUser.value = repository.getCurrentUserData()
                _authState.value = AuthState.Authenticated
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.register(email, password, name)

            result.onSuccess { user ->
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    exception.message ?: "Đăng ký thất bại"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.login(email, password)

            result.onSuccess { user ->
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
            }.onFailure { exception ->
                _authState.value = AuthState.Error(
                    getErrorMessage(exception)
                )
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("password") == true ->
                "Mật khẩu không đúng"
            exception.message?.contains("email") == true ->
                "Email không hợp lệ"
            exception.message?.contains("user") == true ->
                "Tài khoản không tồn tại"
            exception.message?.contains("network") == true ->
                "Lỗi kết nối mạng"
            else -> exception.message ?: "Đã có lỗi xảy ra"
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

