package com.simenko.qmapp.presentation.ui.user.verification

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.data.repository.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class WaitingForVerificationViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    val userState: StateFlow<UserState> get() = userRepository.userState
    fun resendVerificationEmail() = userRepository.sendVerificationEmail(null)
}