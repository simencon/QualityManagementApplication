package com.simenko.qmapp.ui.auth.registration.termsandconditions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.simenko.qmapp.ui.auth.registration.RegistrationActivity
import com.simenko.qmapp.ui.auth.registration.RegistrationViewModel
import com.simenko.qmapp.ui.theme.QMAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsAndConditionsFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                QMAppTheme {
                    TermsAndConditions(
                        modifier = Modifier
                            .padding(all = 0.dp)
                            .fillMaxWidth(),
                    ) {
                        registrationViewModel.acceptTCs()
                        (activity as RegistrationActivity).onTermsAndConditionsAccepted()
                    }
                }
            }
        }
    }
}

@Composable
fun TermsAndConditions(
    modifier: Modifier,
    onClickRegister: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(all = 0.dp)
    ) {
        Text(
            text = "Terms and Conditions",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
        )
        Text(
            text = "Here will be terms and conditions later ...",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
        )
        TextButton(onClick = { onClickRegister() }, content = {
            Text(
                text = "Register",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, start = 0.dp, end = 0.dp, bottom = 0.dp)
            )
        })
    }
}

@Preview(name = "Lite Mode Terms and Conditions", showBackground = true, widthDp = 360)
@Composable
fun TermsAndConditionsPreview() {
    QMAppTheme {
        TermsAndConditions(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp),
            onClickRegister = {}
        )
    }
}
