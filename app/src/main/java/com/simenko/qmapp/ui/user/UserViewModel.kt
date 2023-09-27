package com.simenko.qmapp.ui.user

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.ui.common.TopScreenState
import com.simenko.qmapp.ui.main.createMainActivityIntent
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.REGISTRATION_ROOT
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Named

private const val TAG = "UserViewModel"
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @Named("UserActivity") private val appNavigator: AppNavigator,
    private val topScreenState: TopScreenState
) : ViewModel() {
    val navigationChannel = appNavigator.navigationChannel
    val topScreenChannel = topScreenState.topScreenChannel

    fun logAppNavigator() {
        Log.d(TAG, "logAppNavigator: $appNavigator")
    }

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

    val userState: StateFlow<UserState> get() = userRepository.userState

    private fun updateCurrentUserState() {
        updateLoadingState(Pair(true, null))
        userRepository.getActualUserState()
    }

    fun onStateIsNoState() {
        Log.d(TAG, "onStateIsNoState")
        appNavigator.tryNavigateTo(route = Route.LoggedOut.InitialScreen.link, popUpToRoute = Route.LoggedOut.InitialScreen.route, inclusive = true)
        updateCurrentUserState()
    }

    fun onStateIsUnregisteredState() {
        Log.d(TAG, "onStateIsUnregisteredState")
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = REGISTRATION_ROOT, popUpToId = 0, inclusive = true)
    }

    suspend fun onStateIsUserNeedToVerifyEmailState(msg: String) {
        Log.d(TAG, "onStateIsUserNeedToVerifyEmailState")
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.WaitingForValidation.withArgs(msg), popUpToId = 0, inclusive = true)
        delay(5000)
        updateCurrentUserState()
    }

    suspend fun onStateIsUserAuthoritiesNotVerifiedState(msg: String) {
        Log.d(TAG, "onStateIsUserAuthoritiesNotVerifiedState")
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.WaitingForValidation.withArgs(msg), popUpToId = 0, inclusive = true)
        delay(5000)
        updateCurrentUserState()
    }

    fun onStateIsUserLoggedOutState() {
        Log.d("InitialScreen", "onStateIsUserLoggedOutState")
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.LogIn.link, popUpToRoute = Route.LoggedOut.LogIn.route, inclusive = true)
    }

    fun onStateIsUserLoggedInState(context: Context) {
        Log.d(TAG, "onStateIsUserLoggedInState")
        ContextCompat.startActivity(context, createMainActivityIntent(context), null)
    }
}