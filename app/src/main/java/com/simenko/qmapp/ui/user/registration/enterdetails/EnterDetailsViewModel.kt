package com.simenko.qmapp.ui.user.registration.enterdetails

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.ui.main.main.page.TopScreenState
import com.simenko.qmapp.ui.main.main.AddEditMode
import com.simenko.qmapp.ui.navigation.AppNavigator
import com.simenko.qmapp.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val MIN_LENGTH = 6

@HiltViewModel
class EnterDetailsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val topScreenState: TopScreenState,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _fillInState = MutableStateFlow<FillInState>(FillInInitialState)
    private fun resetToInitialState() {
        _fillInState.value = FillInInitialState
    }

    val fillInState: StateFlow<FillInState> get() = _fillInState

    private var _rawPrinciple: MutableStateFlow<Principle> = MutableStateFlow(userRepository.user.copy())
    private var _rawPrincipleErrors: MutableStateFlow<UserErrors> = MutableStateFlow(UserErrors())
    val rawPrinciple: StateFlow<Principle> get() = _rawPrinciple
    val rawPrincipleErrors: StateFlow<UserErrors> get() = _rawPrincipleErrors

    fun setFullName(value: String) {
        _rawPrinciple.value = _rawPrinciple.value.copy(fullName = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(fullNameError = false)
    }

    fun setDepartment(value: String) {
        _rawPrinciple.value = _rawPrinciple.value.copy(department = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(departmentError = false)
    }

    fun setSubDepartment(value: String?) {
        _rawPrinciple.value = _rawPrinciple.value.copy(subDepartment = value)
    }

    fun setJobRole(value: String) {
        _rawPrinciple.value = _rawPrinciple.value.copy(jobRole = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(jobRoleError = false)
    }

    fun setEmail(value: String) {
        _rawPrinciple.value = _rawPrinciple.value.copy(email = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(emailError = false)
    }

    fun setPhoneNumber(value: Long) {
        _rawPrinciple.value = _rawPrinciple.value.copy(phoneNumber = value)
    }

    fun setPassword(value: String) {
        _rawPrinciple.value = _rawPrinciple.value.copy(password = value)
        _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(passwordError = false)
    }

    fun validateInput(principle: Principle = _rawPrinciple.value) {
        val errorMsg = buildString {
            if (principle.fullName.isEmpty()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(fullNameError = true)
                append("Full name field is mandatory\n")
            }
            if (principle.department.isEmpty()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(departmentError = true)
                append("Department field is mandatory\n")
            }
            if (principle.jobRole.isEmpty()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(jobRoleError = true)
                append("Job role field is mandatory\n")
            }
            if (principle.email.isEmpty()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(emailError = true)
                append("Email field is mandatory\n")
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(principle.email).matches()) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(emailError = true)
                append("Wrong email format\n")
            }
            if (principle.password.length < MIN_LENGTH) {
                _rawPrincipleErrors.value = _rawPrincipleErrors.value.copy(passwordError = true)
                append("Password has to be longer than 6 characters")
            }
        }

        if (errorMsg.isNotEmpty()) _fillInState.value = FillInError(errorMsg)
        else _fillInState.value = FillInSuccess
    }

    fun initRawUser() {
        userRepository.rawUser = _rawPrinciple.value
    }

    fun onFillInSuccess(fullName: String) {
        initRawUser()
        resetToInitialState()
        appNavigator.tryNavigateTo(Route.LoggedOut.Registration.TermsAndConditions.withArgs(fullName))
    }

    fun onLogInClick() {
        appNavigator.tryNavigateTo(Route.LoggedOut.LogIn.link)
    }

    fun onSaveUserDataClick() {
        userRepository.rawUser?.let {
            topScreenState.trySendLoadingState(Pair(true, null))
            userRepository.editUserData(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    topScreenState.trySendLoadingState(Pair(false, null))
                    appNavigator.tryNavigateTo(
                        route = Route.Main.Settings.UserDetails.link,
                        popUpToRoute = Route.Main.Settings.UserDetails.route,
                        inclusive = true
                    )
                } else {
                    topScreenState.trySendLoadingState(Pair(false, task.exception?.message))
                }
            }
        }
    }

    private fun updateUserData() {
        topScreenState.trySendLoadingState(Pair(true, null))
        userRepository.updateUserData()
    }

    fun setupTopScreen() {
//        todo-me: ToDo make in proper way later
        /*topScreenState.trySendTopScreenSetup(Pair(AddEditMode.ACCOUNT_EDIT) { validateInput() }, { updateUserData() }, {})*/
    }
}

fun Long.phoneNumberToString(): String = if (this == NoRecord.num.toLong()) "" else this.toString()


fun String.stringToPhoneNumber(): Long {
    var filtered = this.filter { it.isDigit() }
    if (filtered.length > 18) {
        filtered = filtered.dropLast(1)
    }
    return if (filtered == EmptyString.str) {
        NoRecord.num.toLong()
    } else {
        filtered.toLong()
    }
}


data class UserErrors(
    var fullNameError: Boolean = false,
    var departmentError: Boolean = false,
    var jobRoleError: Boolean = false,
    var emailError: Boolean = false,
    var passwordError: Boolean = false,
)

sealed class FillInState
object FillInInitialState : FillInState()
object FillInSuccess : FillInState()
data class FillInError(val errorMsg: String) : FillInState()