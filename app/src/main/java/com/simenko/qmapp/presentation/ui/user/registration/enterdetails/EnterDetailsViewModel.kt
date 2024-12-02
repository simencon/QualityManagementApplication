package com.simenko.qmapp.presentation.ui.user.registration.enterdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.FillInErrorState
import com.simenko.qmapp.domain.FillInInitialState
import com.simenko.qmapp.domain.FillInState
import com.simenko.qmapp.domain.FillInSuccessState
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.data.cache.prefs.model.Principal
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MIN_LENGTH = 6

@HiltViewModel
class EnterDetailsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val userRepository: UserRepository,
) : ViewModel() {
    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    private var mainPageHandler: MainPageHandler? = null

    fun onEntered(isUserEditMode: Boolean) {
        viewModelScope.launch {
            mainPageHandler = MainPageHandler.Builder(if (isUserEditMode) Page.ACCOUNT_EDIT else Page.EMPTY_PAGE, mainPageState)
                .setOnNavMenuClickAction {
                    appNavigator.tryNavigateTo(route = Route.Main.Settings.UserDetails, popUpToRoute = Route.Main.Settings.UserDetails, inclusive = true)
                }
                .setOnFabClickAction { validateInput() }
                .setOnPullRefreshAction { updateUserData() }
                .build()
                .apply { setupMainPage(0, isUserEditMode) }
        }
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------------------------------
     * */
    private fun updateUserData() {
        userRepository.updateUserData()
    }

    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    private fun resetToInitialState() {
        _fillInState.value = FillInInitialState
    }

    val fillInState: StateFlow<FillInState> get() = _fillInState

    private var _rawPrincipal: MutableStateFlow<Principal> = MutableStateFlow(userRepository.profile)
    private var _rawPrincipleErrors: MutableStateFlow<UserErrors> = MutableStateFlow(UserErrors())
    val rawPrincipal: StateFlow<Principal> get() = _rawPrincipal
    val rawPrincipleErrors: StateFlow<UserErrors> get() = _rawPrincipleErrors

    fun setFullName(value: String) {
        _rawPrincipal.value = _rawPrincipal.value.copy(fullName = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(fullNameError = false)
    }

    fun setDepartment(value: String) {
        _rawPrincipal.value = _rawPrincipal.value.copy(department = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(departmentError = false)
    }

    fun setSubDepartment(value: String?) {
        _rawPrincipal.value = _rawPrincipal.value.copy(subDepartment = value?: EmptyString.str)
    }

    fun setJobRole(value: String) {
        _rawPrincipal.value = _rawPrincipal.value.copy(jobRole = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(jobRoleError = false)
    }

    fun setEmail(value: String) {
        _rawPrincipal.value = _rawPrincipal.value.copy(email = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(emailError = false)
    }

    fun setPhoneNumber(value: String?) {
        if (_rawPrincipal.value.phoneNumber != value) {
            val phoneRegex = Regex("^[+]?[0-9]{0,15}\$")
            if (value?.matches(phoneRegex) != true) return
            _rawPrincipal.value = _rawPrincipal.value.copy(phoneNumber = value)
        }
    }

    fun setPassword(value: String) {
        _rawPrincipal.value = _rawPrincipal.value.copy(password = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(passwordError = false)
    }

    fun validateInput(principal: Principal = _rawPrincipal.value) {
        val errorMsg = buildString {
            if (principal.fullName.isEmpty()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(fullNameError = true)
                append("Full name field is mandatory\n")
            }
            if (principal.department.isEmpty()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(departmentError = true)
                append("Department field is mandatory\n")
            }
            if (principal.jobRole.isEmpty()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(jobRoleError = true)
                append("Job role field is mandatory\n")
            }
            if (principal.email.isEmpty()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(emailError = true)
                append("Email field is mandatory\n")
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(principal.email).matches()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(emailError = true)
                append("Wrong email format\n")
            }
            if (principal.password.length < MIN_LENGTH) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(passwordError = true)
                append("Password has to be longer than 6 characters")
            }
        }

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInErrorState(errorMsg)
        else _fillInState.value = FillInSuccessState
    }

    fun initRawUser() {
        userRepository.rawUser = _rawPrincipal.value
    }

    fun onFillInSuccess(fullName: String) {
        initRawUser()
        resetToInitialState()
        appNavigator.tryNavigateTo(Route.LoggedOut.Registration.TermsAndConditions(fullName))
    }

    fun onLogInClick() {
        appNavigator.tryNavigateTo(Route.LoggedOut.LogIn)
    }

    fun onSaveUserDataClick() {
        userRepository.rawUser?.let {
            mainPageHandler?.updateLoadingState?.invoke(Triple(false, true, null))
            userRepository.editUserData(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, null))
                    appNavigator.tryNavigateTo(route = Route.Main.Settings.UserDetails, popUpToRoute = Route.Main, inclusive = true)
                } else {
                    mainPageHandler?.updateLoadingState?.invoke(Triple(false, false, task.exception?.message))
                }
            }
        }
    }
}


data class UserErrors(
    var fullNameError: Boolean = false,
    var departmentError: Boolean = false,
    var jobRoleError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,
)