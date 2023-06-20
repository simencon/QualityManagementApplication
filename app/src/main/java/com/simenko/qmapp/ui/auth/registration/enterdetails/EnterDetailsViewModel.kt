package com.simenko.qmapp.ui.auth.registration.enterdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val MAX_LENGTH = 5

/**
 * EnterDetailsViewModel is the ViewModel that [EnterDetailsFragment] uses to
 * obtain to validate user's input data.
 */
@HiltViewModel
class EnterDetailsViewModel @Inject constructor() : ViewModel() {

    private val _enterDetailsState = MutableLiveData<EnterDetailsViewState>()
    val enterDetailsState: LiveData<EnterDetailsViewState>
        get() = _enterDetailsState

    fun validateInput(username: String, password: String) {
        when {
            username.length < MAX_LENGTH -> _enterDetailsState.value =
                EnterDetailsError("Username has to be longer than 4 characters")
            password.length < MAX_LENGTH -> _enterDetailsState.value =
                EnterDetailsError("Password has to be longer than 4 characters")
            else -> _enterDetailsState.value = EnterDetailsSuccess
        }
    }
}

sealed class EnterDetailsViewState
object EnterDetailsSuccess : EnterDetailsViewState()
data class EnterDetailsError(val error: String) : EnterDetailsViewState()