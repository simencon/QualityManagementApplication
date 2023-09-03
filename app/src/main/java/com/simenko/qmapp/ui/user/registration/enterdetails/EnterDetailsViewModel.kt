package com.simenko.qmapp.ui.user.registration.enterdetails

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.storage.Principle
import com.simenko.qmapp.storage.Storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val MIN_LENGTH = 6

/**
 * EnterDetailsViewModel is the ViewModel that [EnterDetailsFragment] uses to
 * obtain to validate user's input data.
 */
@HiltViewModel
class EnterDetailsViewModel @Inject constructor(
    private val storage: Storage
) : ViewModel() {

    private val _enterDetailsState = MutableStateFlow<Event<EnterDetailsViewState>>(Event(EnterDetailsInitialState))
    val enterDetailsState: StateFlow<Event<EnterDetailsViewState>> get() = _enterDetailsState

    private var _rawPrinciple: MutableStateFlow<Principle> = MutableStateFlow(Principle(storage))
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

    fun validateInput(principle: Principle) {
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

        if (errorMsg.isNotEmpty()) _enterDetailsState.value = Event(EnterDetailsError(errorMsg))
        else _enterDetailsState.value = Event(EnterDetailsSuccess)
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

sealed class EnterDetailsViewState
object EnterDetailsInitialState : EnterDetailsViewState()
object EnterDetailsSuccess : EnterDetailsViewState()
data class EnterDetailsError(val errorMsg: String) : EnterDetailsViewState()