package com.simenko.qmapp.ui.user.login

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.ui.user.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * LoginViewModel is the ViewModel that [LoginActivity] uses to
 * obtain information of what to show on the screen and handle complex logic.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private lateinit var _userViewModel: UserViewModel
    fun initUserViewModel(model: UserViewModel) {
        _userViewModel = model
    }

    fun updateLoadingState(state: Pair<Boolean, String?>) {
        _userViewModel.updateLoadingState(state)
    }

    val userState: StateFlow<UserState>
        get() = _userViewModel.userState

    fun login(username: String, password: String) {
        _userViewModel.updateLoadingState(Pair(true, null))
        userRepository.loginUser(username, password)
    }

    fun getUserEmail(): String {
        return userRepository.user.email
    }

    fun sendResetPasswordEmail(email: String) {
        userRepository.sendResetPasswordEmail(email)
    }
}