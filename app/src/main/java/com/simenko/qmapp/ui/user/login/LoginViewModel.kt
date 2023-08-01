package com.simenko.qmapp.ui.user.login

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * LoginViewModel is the ViewModel that [LoginActivity] uses to
 * obtain information of what to show on the screen and handle complex logic.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {
    val userState: StateFlow<Event<UserState>>
        get() = userRepository.userState

    fun login(username: String, password: String) {
        userRepository.loginUser(username, password)
    }

    fun getUserEmail(): String {
        return userRepository.user.email
    }

    fun sendResetPasswordEmail(email: String) {
        userRepository.sendResetPasswordEmail(email)
    }
}