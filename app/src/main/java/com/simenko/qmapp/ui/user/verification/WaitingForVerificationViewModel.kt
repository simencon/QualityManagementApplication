package com.simenko.qmapp.ui.user.verification

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * LoginViewModel is the ViewModel that [LoginActivity] uses to
 * obtain information of what to show on the screen and handle complex logic.
 */
@HiltViewModel
class WaitingForVerificationViewModel @Inject constructor(private val userManager: UserRepository): ViewModel() {
    val userState: StateFlow<Event<UserState>>
        get() = userManager.userState
}