package com.simenko.qmapp.ui.user

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.createMainActivityIntent
import com.simenko.qmapp.ui.main.main.TopScreenIntent
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.REGISTRATION_ROUTE
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.navigation.subscribeNavigationEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    val navHostController: NavHostController
) : ViewModel() {
    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    init {
        subscribeMainScreenSetupEvents(mainPageState.topScreenChannel.receiveAsFlow())
        appNavigator.navigationChannel.receiveAsFlow().subscribeNavigationEvents(viewModelScope, navHostController)
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
    private val _isLoadingInProgress = MutableStateFlow(true)
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

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onStateIsNoState() {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.InitialScreen.link, popUpToRoute = Route.LoggedOut.InitialScreen.route, inclusive = true)
        updateCurrentUserState()
    }

    fun onStateIsUnregisteredState() {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = REGISTRATION_ROUTE, popUpToId = 0, inclusive = true)
    }

    suspend fun onStateIsUserNeedToVerifyEmailState(msg: String) {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.WaitingForValidation.withArgs(msg), popUpToId = 0, inclusive = true)
        delay(5000)
        updateCurrentUserState()
    }

    suspend fun onStateIsUserAuthoritiesNotVerifiedState(msg: String) {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.WaitingForValidation.withArgs(msg), popUpToId = 0, inclusive = true)
        delay(5000)
        updateCurrentUserState()
    }

    fun onStateIsUserLoggedOutState() {
        updateLoadingState(Pair(false, null))
        appNavigator.tryNavigateTo(route = Route.LoggedOut.LogIn.link, popUpToRoute = Route.LoggedOut.LogIn.route, inclusive = true)
    }

    fun onStateIsUserLoggedInState(context: Context) {
        ContextCompat.startActivity(context, createMainActivityIntent(context), null)
    }
}