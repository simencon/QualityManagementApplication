package com.simenko.qmapp.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simenko.qmapp.ui.theme.*
import com.simenko.qmapp.ui.user.registration.RegistrationViewModel

@Composable
fun UserExistDialog(
    modifier: Modifier = Modifier,
    registrationViewModel: RegistrationViewModel,
    msg: String,
    onChangeEmail: () -> Unit,
    onLoginClick: () -> Unit
) {
    var enableToEdit by rememberSaveable { mutableStateOf(false) }
    var placeHolder by rememberSaveable { mutableStateOf("") }

    Dialog(
        onDismissRequest = { registrationViewModel.hideUserExistDialog() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(10.dp),
            // modifier = modifier.size(280.dp, 240.dp)
            modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 10.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier.background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //.......................................................................
                Image(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null, // decorative
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        color = Primary
                    ),
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .height(50.dp)
                        .fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 14.sp, color = MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(all = 5.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                //.......................................................................
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(Primary),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = onChangeEmail
                    ) {
                        Text(
                            "Change email",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = onLoginClick
                    ) {
                        Text(
                            "Login",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

