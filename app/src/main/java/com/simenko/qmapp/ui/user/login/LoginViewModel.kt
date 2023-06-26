package com.simenko.qmapp.ui.user.login

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.ui.user.repository.UserManager
import com.simenko.qmapp.ui.user.repository.UserRegisteredState
import com.simenko.qmapp.ui.user.repository.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * LoginViewModel is the ViewModel that [LoginActivity] uses to
 * obtain information of what to show on the screen and handle complex logic.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val userManager: UserManager): ViewModel() {
    val userState: StateFlow<Event<UserState>>
        get() = userManager.userState

    fun login(username: String, password: String) {
        userManager.loginUser(username, password)
    }

    fun unregister() {
        userManager.unregister()
    }

    fun getUsername(): String {
        return userManager.username
    }
}