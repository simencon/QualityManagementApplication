package com.simenko.qmapp.ui.user.registration

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.main.AddEditMode
import com.simenko.qmapp.ui.main.MainActivityViewModel
import com.simenko.qmapp.ui.user.UserViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private lateinit var _userViewModel: UserViewModel
    fun initUserViewModel(model: UserViewModel) {
        _userViewModel = model
    }

    fun updateLoadingState(state: Pair<Boolean, String?>) {
        _userViewModel.updateLoadingState(state)
    }

    private lateinit var _mainViewModel: MainActivityViewModel
    fun initMainViewModel(model: MainActivityViewModel) {
        _mainViewModel = model
    }

    fun updateMeinLoadingState(state: Pair<Boolean, String?>) {
        _mainViewModel.updateLoadingState(state)
    }

    fun setAddEditMode(value: AddEditMode) {
        _mainViewModel.setAddEditMode(value)
    }

    val userState: StateFlow<UserState> get() = _userViewModel.userState

    private var _principleToRegister: Principle? = null

    private var _acceptedTCs: Boolean? = null

    fun initPrincipleToRegister(principle: Principle) {
        _principleToRegister = principle
    }

    fun registerUser() {
        assert(_principleToRegister != null)
        _userViewModel.updateLoadingState(Pair(true, null))
        userRepository.registerUser(_principleToRegister!!)
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
}
