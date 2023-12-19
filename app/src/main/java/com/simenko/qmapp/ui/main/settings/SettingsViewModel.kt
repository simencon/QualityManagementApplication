package com.simenko.qmapp.ui.main.settings

import androidx.lifecycle.ViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.simenko.qmapp.domain.TrueStr
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.main.main.MainPageHandler
import com.simenko.qmapp.ui.main.main.MainPageState
import com.simenko.qmapp.ui.main.main.content.Page
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val userRepository: UserRepository,
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {
    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler

    init {
        mainPageHandler = MainPageHandler.Builder(Page.ACCOUNT_SETTINGS, mainPageState)
            .setOnPullRefreshAction { this.updateUserData() }
            .build()
        val restApiUrl = remoteConfig.getString("app_rest_api_url")
        println("SettingsViewModel remote config value: ${restApiUrl}, chars: ${restApiUrl.length}")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onUserDataEditClick() {
        appNavigator.tryNavigateTo(Route.Main.Settings.EditUserDetails.withArgs(TrueStr.str))
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _isApproveActionVisible = MutableStateFlow(false)
    val isApproveActionVisible: StateFlow<Boolean> = _isApproveActionVisible
    fun hideActionApproveDialog() {
        _isApproveActionVisible.value = false
    }

    fun showActionApproveDialog() {
        _isApproveActionVisible.value = true
    }

    val userState: StateFlow<UserState> get() = userRepository.userState
    val userLocalData: Principle get() = userRepository.user

    fun clearLoadingState(error: String? = null) {
        mainPageHandler.updateLoadingState(Pair(false, error))
        userRepository.clearErrorMessage()
    }

    fun logout() {
        mainPageHandler.updateLoadingState(Pair(true, null))
        userRepository.logout()
    }

    fun deleteAccount(userEmail: String, password: String) {
        mainPageHandler.updateLoadingState(Pair(true, null))
        userRepository.deleteAccount(userEmail, password)
    }

    private fun updateUserData() {
        mainPageHandler.updateLoadingState(Pair(true, null))
        userRepository.updateUserData()
    }
}