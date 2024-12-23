package com.simenko.qmapp.presentation.ui.user.registration

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.data.repository.UserState
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState
) : ViewModel() {
    fun updateLoadingState(state: Triple<Boolean, Boolean, String?>) {
        mainPageState.trySendLoadingState(state)
    }

    val userState: StateFlow<UserState> get() = userRepository.userState

    fun registerUser() {
        assert(userRepository.rawUser != null)
        updateLoadingState(Triple(false, true, null))
        userRepository.registerUser(userRepository.rawUser!!)
    }

    private val _isUserExistDialogVisible = MutableStateFlow(false)
    val isUserExistDialogVisible: StateFlow<Boolean> = _isUserExistDialogVisible
    fun hideUserExistDialog() {
        updateLoadingState(Triple(false, false, null))
        userRepository.clearUserData()
        _isUserExistDialogVisible.value = false
    }

    fun showUserExistDialog() {
        _isUserExistDialogVisible.value = true
    }

    fun onChangeRegistrationEmailClick() {
        hideUserExistDialog()
        appNavigator.tryNavigateBack()
    }

    fun onProceedToLoginClick() {
        hideUserExistDialog()
        appNavigator.tryNavigateTo(route = Route.LoggedOut.LogIn)
    }
}
