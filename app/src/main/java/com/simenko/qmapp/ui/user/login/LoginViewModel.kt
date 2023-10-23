package com.simenko.qmapp.ui.user.login

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.user.registration.enterdetails.UserErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val mainPageState: MainPageState
) : ViewModel() {
    fun updateLoadingState(state: Pair<Boolean, String?>) {
        mainPageState.trySendLoadingState(state)
    }

    private var _loggedOutPrinciple: MutableStateFlow<Principle> = MutableStateFlow(userRepository.user.copy(password = EmptyString.str))
    private var _loggedOutPrincipleErrors: MutableStateFlow<UserErrors> = MutableStateFlow(UserErrors())
    val loggedOutPrinciple: StateFlow<Principle> get() = _loggedOutPrinciple
    val loggedOutPrincipleErrors: StateFlow<UserErrors> get() = _loggedOutPrincipleErrors
    val userState: StateFlow<UserState> get() = userRepository.userState

    fun setEmail(value: String) {
        _loggedOutPrinciple.value = _loggedOutPrinciple.value.copy(email = value)
        _loggedOutPrincipleErrors.value = _loggedOutPrincipleErrors.value.copy(emailError = false)
    }

    fun setPassword(value: String) {
        _loggedOutPrinciple.value = _loggedOutPrinciple.value.copy(password = value)
        _loggedOutPrincipleErrors.value = _loggedOutPrincipleErrors.value.copy(passwordError = false)
    }

    fun login(username: String, password: String) {
        updateLoadingState(Pair(true, null))
        userRepository.loginUser(username, password)
    }

    fun sendResetPasswordEmail(email: String) {
        updateLoadingState(Pair(true, null))
        userRepository.sendResetPasswordEmail(email)
    }
}