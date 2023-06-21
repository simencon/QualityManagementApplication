package com.simenko.qmapp.ui.user.settings

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.ui.user.user.UserDataRepository
import com.simenko.qmapp.ui.user.user.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * SettingsViewModel is the ViewModel that [SettingsActivity] uses to handle complex logic.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val userManager: UserManager
) : ViewModel() {

    fun refreshNotifications() {
        userDataRepository.refreshUnreadNotifications()
    }

    fun logout() {
        userManager.logout()
    }
}
