package com.simenko.qmapp.ui.user

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.ui.main.createMainActivityIntent
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    val appNavigator: AppNavigator,
    private val userRepository: UserRepository
) : ViewModel() {
    val navigationChannel = appNavigator.navigationChannel

    private val _isLoadingInProgress = MutableStateFlow(false)
    val isLoadingInProgress: StateFlow<Boolean> get() = _isLoadingInProgress
    private val _isErrorMessage = MutableStateFlow<String?>(null)
    val isErrorMessage: StateFlow<String?> get() = _isErrorMessage

    fun updateLoadingState(state: Pair<Boolean, String?>) {
        _isLoadingInProgress.value = state.first
        _isErrorMessage.value = state.second
    }

    fun onNetworkErrorShown() {
        _isLoadingInProgress.value = false
        _isErrorMessage.value = null
    }

    val userState: StateFlow<UserState>
        get() = userRepository.userState

    fun updateCurrentUserState() {
//        userRepository.clearErrorMessage()
        updateLoadingState(Pair(true, null))
        userRepository.getActualUserState()
    }

    fun onStateIsNoState() {
        appNavigator.tryNavigateTo(Route.LoggedOut.InitialScreen.link)
        updateCurrentUserState()
    }

    fun onStateIsUnregisteredState() {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(Route.LoggedOut.Registration.link)
    }

    suspend fun onStateIsUserNeedToVerifyEmailState(msg: String) {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(Route.LoggedOut.WaitingForValidation.withArgs(msg))
        delay(5000)
        updateCurrentUserState()
    }

    suspend fun onStateIsUserAuthoritiesNotVerifiedState(msg: String) {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(Route.LoggedOut.WaitingForValidation.withArgs(msg))
        delay(5000)
        updateCurrentUserState()
    }

    fun onStateIsUserLoggedOutState() {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(Route.LoggedOut.LogIn.link)
    }

    fun onStateIsUserLoggedInState(context: Context) {
        ContextCompat.startActivity(context, createMainActivityIntent(context), null)
    }
}