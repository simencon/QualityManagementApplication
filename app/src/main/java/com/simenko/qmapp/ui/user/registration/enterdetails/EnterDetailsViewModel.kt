package com.simenko.qmapp.ui.user.registration.enterdetails

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
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
class EnterDetailsViewModel @Inject constructor() : ViewModel() {

    private val _enterDetailsState = MutableStateFlow<Event<EnterDetailsViewState>>(Event(EnterDetailsInitialState))
    val enterDetailsState: StateFlow<Event<EnterDetailsViewState>>
        get() = _enterDetailsState

    fun validateInput(userFullName: String, userDepartment: String, userJobRole: String, userEmail: String, password: String) {
        val userErrors = UserErrors()
        val errorMsg = buildString {
            if (userFullName.isEmpty()) {
                userErrors.fullNameError = true
                append("Full name field is mandatory\n")
            }
            if (userDepartment.isEmpty()) {
                userErrors.departmentError = true
                append("Department field is mandatory\n")
            }
            if (userJobRole.isEmpty()) {
                userErrors.jobRoleError = true
                append("Job role field is mandatory\n")
            }
            if (userEmail.isEmpty()) {
                userErrors.emailError = true
                append("Email field is mandatory\n")
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                userErrors.emailError = true
                append("Wrong email format\n")
            }
            if (password.length < MIN_LENGTH) {
                userErrors.passwordError = true
                append("Password has to be longer than 6 characters")
            }
        }

        if (errorMsg.isNotEmpty()) _enterDetailsState.value = Event(EnterDetailsError(errorMsg, userErrors))
        else _enterDetailsState.value = Event(EnterDetailsSuccess)
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
data class EnterDetailsError(val errorMsg: String, val errorTarget: UserErrors) : EnterDetailsViewState()