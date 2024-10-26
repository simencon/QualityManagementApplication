package com.simenko.qmapp.presentation.ui.user.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.data.repository.UserState
import com.simenko.qmapp.data.cache.prefs.model.Principal
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.user.registration.enterdetails.UserErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val userRepository: UserRepository,
    private val mainPageState: MainPageState
) : ViewModel() {
    fun updateLoadingState(state: Triple<Boolean, Boolean, String?>) {
        mainPageState.trySendLoadingState(state)
    }

    private var _loggedOutPrincipal: MutableStateFlow<Principal> = MutableStateFlow(userRepository.profile.copy(password = EmptyString.str))
    private var _loggedOutPrincipleErrors: MutableStateFlow<UserErrors> = MutableStateFlow(UserErrors())
    val loggedOutPrincipal: StateFlow<Principal> get() = _loggedOutPrincipal
    val loggedOutPrincipleErrors: StateFlow<UserErrors> get() = _loggedOutPrincipleErrors
    val userState: StateFlow<UserState> get() = userRepository.userState

    fun setEmail(value: String) {
        _loggedOutPrincipal.value = _loggedOutPrincipal.value.copy(email = value)
        _loggedOutPrincipleErrors.value = _loggedOutPrincipleErrors.value.copy(emailError = false)
    }

    fun setPassword(value: String) {
        _loggedOutPrincipal.value = _loggedOutPrincipal.value.copy(password = value)
        _loggedOutPrincipleErrors.value = _loggedOutPrincipleErrors.value.copy(passwordError = false)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun login(username: String, password: String) {
        viewModelScope.launch {
//            userRepository.clearErrorMessage()
            updateLoadingState(Triple(false, true, null))
            userRepository.loginUser(username, password)
        }
    }

    fun sendResetPasswordEmail(email: String) {
        updateLoadingState(Triple(false, true, null))
        userRepository.sendResetPasswordEmail(email)
    }

    fun onRegisterNewAccount() {
        userRepository.clearUserData()
        appNavigator.tryNavigateTo(route = Route.LoggedOut.Registration, popUpToRoute = Route.LoggedOut, inclusive = true)
    }
}