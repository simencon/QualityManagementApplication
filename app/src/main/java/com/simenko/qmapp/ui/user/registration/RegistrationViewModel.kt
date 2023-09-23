package com.simenko.qmapp.ui.user.registration

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.REGISTRATION_ROOT
import com.simenko.qmapp.ui.navigation.Route
import com.simenko.qmapp.ui.user.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val userRepository: UserRepository
) : ViewModel() {
    private lateinit var _userViewModel: UserViewModel
    fun initUserViewModel(model: UserViewModel) {
        _userViewModel = model
    }

    fun updateLoadingState(state: Pair<Boolean, String?>) {
        _userViewModel.updateLoadingState(state)
    }

    val userState: StateFlow<UserState> get() = _userViewModel.userState

    fun registerUser() {
        assert(userRepository.rawUser != null)
        _userViewModel.updateLoadingState(Pair(true, null))
        userRepository.registerUser(userRepository.rawUser!!)
    }

    private val _isUserExistDialogVisible = MutableStateFlow(false)
    val isUserExistDialogVisible: StateFlow<Boolean> = _isUserExistDialogVisible
    fun hideUserExistDialog() {
        _userViewModel.updateLoadingState(Pair(false, null))
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
