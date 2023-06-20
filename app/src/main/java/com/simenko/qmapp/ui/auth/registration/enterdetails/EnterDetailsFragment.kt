package com.simenko.qmapp.ui.auth.registration.enterdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simenko.qmapp.ui.auth.registration.RegistrationActivity
import com.simenko.qmapp.ui.auth.registration.RegistrationViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterDetailsFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                QMAppTheme {
                    EnterDetails(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxWidth(),
                        registrationViewModel = registrationViewModel,
                        onSuccess = {
                            (activity as RegistrationActivity).onDetailsEntered()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EnterDetails(
    modifier: Modifier,
    registrationViewModel: RegistrationViewModel = hiltViewModel(),
    enterDetailsViewModel: EnterDetailsViewModel = hiltViewModel(),
    onSuccess: () -> Unit = {}
) {
    val state by enterDetailsViewModel.enterDetailsState.observeAsState()

    var userNameText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }
    var errorText by rememberSaveable { mutableStateOf("") }

    when (state) {
        is EnterDetailsSuccess -> {
            registrationViewModel.updateUserData(userNameText, passwordText)
            onSuccess()
        }
        is EnterDetailsError -> {
            errorText = (state as EnterDetailsError).error
        }
        else -> {}
    }

    Column(
        modifier = Modifier
            .padding(all = 0.dp)
    ) {
        Text(
            text = "Register to Quality Management",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
        )
        TextField(value = userNameText, onValueChange = { userNameText = it })
        TextField(value = passwordText, onValueChange = { passwordText = it })
        if (state is EnterDetailsError)
            Text(
                text = errorText,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
        TextButton(
            onClick = {
                enterDetailsViewModel.validateInput(userNameText, passwordText)
            },
            content = {
                Text(
                    text = "Next",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
                )
            })
    }
}

@Preview(name = "Lite Mode Enter Details", showBackground = true, widthDp = 360)
@Composable
fun EnterDetailsPreview() {
    QMAppTheme {
        EnterDetails(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp)
        )
    }
}