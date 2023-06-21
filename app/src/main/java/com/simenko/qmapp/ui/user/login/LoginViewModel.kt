package com.simenko.qmapp.ui.user.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.ui.user.model.UserManager
import com.simenko.qmapp.ui.user.registration.enterdetails.EnterDetailsViewState
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

    private val _loginState = MutableStateFlow<Event<LoginViewState>>(Event(LoginInitialState))
    val loginState: StateFlow<Event<LoginViewState>>
        get() = _loginState

    fun login(username: String, password: String) {
        if (userManager.loginUser(username, password)) {
            _loginState.value = Event(LoginSuccess)
        } else {
            _loginState.value = Event(LoginError)
        }
    }

    fun unregister() {
        userManager.unregister()
    }

    fun getUsername(): String = userManager.username
}

sealed class LoginViewState
object LoginInitialState : LoginViewState()
object LoginSuccess : LoginViewState()
object LoginError : LoginViewState()
