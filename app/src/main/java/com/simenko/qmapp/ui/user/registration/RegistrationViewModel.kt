package com.simenko.qmapp.ui.user.registration

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _loadingState: MutableStateFlow<Pair<Boolean, String?>> = MutableStateFlow(Pair(false, null))
    val loadingState get() = _loadingState.asStateFlow()

    fun updateLoadingState(state: Pair<Boolean, String?>) {
        _loadingState.value = state
    }

    val userState: StateFlow<UserState> get() = userRepository.userState

    fun registerUser() {
        assert(userRepository.rawUser != null)
        _loadingState.value = Pair(true, null)
        userRepository.registerUser(userRepository.rawUser!!)
    }

    private val _isUserExistDialogVisible = MutableStateFlow(false)
    val isUserExistDialogVisible: StateFlow<Boolean> = _isUserExistDialogVisible
    fun hideUserExistDialog() {
        _loadingState.value = Pair(false, null)
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
        appNavigator.tryNavigateTo(route = Route.LoggedOut.LogIn.link)
    }
}
