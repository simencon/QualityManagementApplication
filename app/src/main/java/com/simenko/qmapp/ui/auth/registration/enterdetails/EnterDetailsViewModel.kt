package com.simenko.qmapp.ui.auth.registration.enterdetails

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val MAX_LENGTH = 5

/**
 * EnterDetailsViewModel is the ViewModel that [EnterDetailsFragment] uses to
 * obtain to validate user's input data.
 */
@HiltViewModel
class EnterDetailsViewModel @Inject constructor() : ViewModel() {

    private val _enterDetailsState = MutableStateFlow<Event<EnterDetailsViewState>>(Event(EnterDetailsInitialState))
    val enterDetailsState: StateFlow<Event<EnterDetailsViewState>>
        get() = _enterDetailsState

    fun validateInput(username: String, password: String) {
        when {
            username.length < MAX_LENGTH -> _enterDetailsState.value =
                Event(EnterDetailsError("Username has to be longer than 4 characters"))
            password.length < MAX_LENGTH -> _enterDetailsState.value =
                Event(EnterDetailsError("Password has to be longer than 4 characters"))
            else -> _enterDetailsState.value = Event(EnterDetailsSuccess)
        }
    }
}

sealed class EnterDetailsViewState

object EnterDetailsInitialState : EnterDetailsViewState()
object EnterDetailsSuccess : EnterDetailsViewState()
data class EnterDetailsError(val error: String) : EnterDetailsViewState()