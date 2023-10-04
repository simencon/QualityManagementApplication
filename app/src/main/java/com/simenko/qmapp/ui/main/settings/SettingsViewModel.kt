package com.simenko.qmapp.ui.main.settings

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.main.main.page.TopPageState
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val topPageState: TopPageState,
    private val userRepository: UserRepository
) : ViewModel() {
    private fun updateLoadingState(state: Pair<Boolean, String?>) {
        topPageState.trySendLoadingState(state)
    }

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
        updateLoadingState(Pair(false, error))
        userRepository.clearErrorMessage()
    }

    fun logout() {
        updateLoadingState(Pair(true, null))
        userRepository.logout()
    }

    fun deleteAccount(userEmail: String, password: String) {
        updateLoadingState(Pair(true, null))
        userRepository.deleteAccount(userEmail, password)
    }

    private fun updateUserData() {
        updateLoadingState(Pair(true, null))
        userRepository.updateUserData()
    }

    fun onUserDataEditClick() {
        appNavigator.tryNavigateTo(Route.Main.Settings.EditUserDetails.link)
    }

    fun setupTopScreen() {
//        todo-me: ToDo make in proper way later
        /*topScreenState.trySendTopScreenSetup(Pair(AddEditMode.NO_MODE) { }, { updateUserData() }, {})*/
    }
}