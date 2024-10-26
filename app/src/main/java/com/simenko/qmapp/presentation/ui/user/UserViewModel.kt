package com.simenko.qmapp.presentation.ui.user

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.data.repository.UserState
import com.simenko.qmapp.presentation.ui.main.MainActivity.Companion.createMainActivityIntent
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.TopScreenIntent
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val userRepository: UserRepository,
    private val mainPageState: MainPageState,
) : ViewModel() {
    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    init {
        subscribeMainScreenSetupEvents(mainPageState.topScreenChannel.receiveAsFlow())
    }

    private fun subscribeMainScreenSetupEvents(intents: Flow<TopScreenIntent>) {
        viewModelScope.launch {
            intents.collect {
                handleEvent(it)
            }
        }
    }

    private fun handleEvent(intent: TopScreenIntent) {
        when (intent) {
            is TopScreenIntent.LoadingState -> updateLoadingState(intent.state)
            else -> {}
        }
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _isSubLoadingInProgress = MutableStateFlow(true)
    val observerSubLoadingProcess: StateFlow<Boolean> get() = _isSubLoadingInProgress

    private val _isLoadingInProgress = MutableStateFlow(true)
    val observerLoadingProcess: StateFlow<Boolean> get() = _isLoadingInProgress

    private val _isErrorMessage = MutableStateFlow<String?>(null)
    val isErrorMessage: StateFlow<String?> get() = _isErrorMessage

    fun updateLoadingState(state: Triple<Boolean, Boolean, String?>) {
        _isSubLoadingInProgress.value = state.first
        _isLoadingInProgress.value = state.second
        _isErrorMessage.value = state.third
    }

    fun onNetworkErrorShown() {
        _isSubLoadingInProgress.value = false
        _isLoadingInProgress.value = false
        _isErrorMessage.value = null
    }

    val userState: StateFlow<UserState> get() = userRepository.userState

    @OptIn(ExperimentalSerializationApi::class)
    private fun updateCurrentUserState() {
        updateLoadingState(Triple(false, true, null))
        userRepository.getActualUserState()
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onStateIsNoState() {
        updateLoadingState(Triple(false, false, null))
        if (userRepository.profile.email.isEmpty()) {
            appNavigator.tryNavigateTo(route = Route.LoggedOut.Registration, popUpToRoute = Route.LoggedOut, inclusive = true)
        } else {
            appNavigator.tryNavigateTo(route = Route.LoggedOut.LogIn, popUpToRoute = Route.LoggedOut, inclusive = true)
        }
        updateCurrentUserState()
    }

    fun onStateIsUnregisteredState() {
        updateLoadingState(Triple(false, false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.Registration, popUpToRoute = Route.LoggedOut, inclusive = true)
    }

    suspend fun onStateIsUserNeedToVerifyEmailState(msg: String) {
        updateLoadingState(Triple(false, false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.WaitingForValidation(msg), Route.LoggedOut, inclusive = true)
        delay(5000)
        updateCurrentUserState()
    }

    suspend fun onStateIsUserAuthoritiesNotVerifiedState(msg: String) {
        updateLoadingState(Triple(false, false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.WaitingForValidation(msg), Route.LoggedOut, inclusive = true)
        delay(5000)
        updateCurrentUserState()
    }

    fun onStateIsUserLoggedOutState() {
        updateLoadingState(Triple(false, false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.LogIn, popUpToRoute = Route.LoggedOut, inclusive = true)
    }

    fun onStateIsUserLoggedInState(context: Context) {
        updateLoadingState(Triple(false, false, null))
        ContextCompat.startActivity(context, createMainActivityIntent(context), null)
    }
}