package com.simenko.qmapp.ui.user.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simenko.qmapp.ui.user.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * LoginViewModel is the ViewModel that [LoginActivity] uses to
 * obtain information of what to show on the screen and handle complex logic.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val userManager: UserManager): ViewModel() {

    private val _loginState = MutableLiveData<LoginViewState>()
    val loginState: LiveData<LoginViewState>
        get() = _loginState

    fun login(username: String, password: String) {
        if (userManager.loginUser(username, password)) {
            _loginState.value = LoginSuccess
        } else {
            _loginState.value = LoginError
        }
    }

    fun unregister() {
        userManager.unregister()
    }

    fun getUsername(): String = userManager.username
}

sealed class LoginViewState
object LoginSuccess : LoginViewState()
object LoginError : LoginViewState()
