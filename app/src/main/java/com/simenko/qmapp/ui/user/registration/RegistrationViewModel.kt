package com.simenko.qmapp.ui.user.registration

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.ui.common.TopScreenIntent
import com.simenko.qmapp.ui.common.TopScreenState
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val topScreenState: TopScreenState
) : ViewModel() {
    suspend fun updateLoadingState(state: Pair<Boolean, String?>) {
        topScreenState.topScreenChannel.send(TopScreenIntent.LoadingState(state))
    }

    val userState: StateFlow<UserState> get() = userRepository.userState

    suspend fun registerUser() {
        assert(userRepository.rawUser != null)
        updateLoadingState(Pair(true, null))
        userRepository.registerUser(userRepository.rawUser!!)
    }

    private val _isUserExistDialogVisible = MutableStateFlow(false)
    val isUserExistDialogVisible: StateFlow<Boolean> = _isUserExistDialogVisible
    suspend fun hideUserExistDialog() {
        updateLoadingState(Pair(false, null))
        userRepository.clearUserData()
        _isUserExistDialogVisible.value = false
    }

    fun showUserExistDialog() {
        _isUserExistDialogVisible.value = true
    }

    suspend fun onChangeRegistrationEmailClick() {
        hideUserExistDialog()
        appNavigator.tryNavigateBack()
    }

    suspend fun onProceedToLoginClick() {
        hideUserExistDialog()
        appNavigator.tryNavigateTo(route = Route.LoggedOut.LogIn.link)
    }
}
