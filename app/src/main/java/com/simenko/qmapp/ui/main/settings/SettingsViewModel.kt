package com.simenko.qmapp.ui.main.settings

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.ui.user.repository.UserManager
import com.simenko.qmapp.ui.user.repository.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * SettingsViewModel is the ViewModel that [SettingsFragment] uses to handle complex logic.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userManager: UserManager
) : ViewModel() {
    val userState: StateFlow<Event<UserState>>
        get() = userManager.userState

    fun logout() {
        userManager.logout()
    }

    fun setUserJobRole(userJobRole: String) {
        userManager.setUserJobRole(userJobRole)
    }
}
