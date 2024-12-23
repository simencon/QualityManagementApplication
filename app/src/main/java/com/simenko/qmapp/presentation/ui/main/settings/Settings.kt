package com.simenko.qmapp.presentation.ui.main.settings

import android.Manifest
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.simenko.qmapp.BuildConfig
import com.simenko.qmapp.domain.EmptyString
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.data.repository.NoState
import com.simenko.qmapp.data.repository.UnregisteredState
import com.simenko.qmapp.data.repository.UserAuthoritiesNotVerifiedState
import com.simenko.qmapp.data.repository.UserError
import com.simenko.qmapp.presentation.theme.QMAppTheme
import com.simenko.qmapp.data.repository.UserErrorState
import com.simenko.qmapp.data.repository.UserLoggedInState
import com.simenko.qmapp.data.repository.UserLoggedOutState
import com.simenko.qmapp.data.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.presentation.ui.common.InfoLine
import com.simenko.qmapp.presentation.ui.common.RecordActionTextBtn
import com.simenko.qmapp.presentation.ui.dialogs.ApproveAction
import com.skydoves.landscapist.rememberDrawablePainter
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Settings(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    onLogOut: () -> Unit,
    onEditUserData: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.mainPageHandler.setupMainPage(0, false)
        viewModel.onEntered()
    }

    val userState by viewModel.userState.collectAsStateWithLifecycle()
    LaunchedEffect(userState) {
        userState.let {
            when (it) {
                is UserErrorState ->
                    if (it.error != UserError.NO_ERROR.error)
                        viewModel.clearLoadingState(it.error ?: UserError.UNKNOWN_ERROR.error)
                    else
                        viewModel.clearLoadingState()

                is UserLoggedOutState, is UnregisteredState, is UserNeedToVerifyEmailState, is UserAuthoritiesNotVerifiedState -> onLogOut()
                is UserLoggedInState, NoState -> viewModel.clearLoadingState()
            }
        }
    }

    val approveActionDialogVisibility by viewModel.isApproveActionVisible.collectAsStateWithLifecycle()

    val onDenyLambda = remember { { viewModel.hideActionApproveDialog() } }
    val onApproveLambda = remember<(String) -> String> {
        {
            if (it == viewModel.profile.password) {
                viewModel.deleteAccount(viewModel.profile.email, it)
                EmptyString.str
            } else {
                UserError.WRONG_PASSWORD.error
            }
        }
    }

    val columnState = rememberScrollState()
    val context = LocalContext.current

    val profilePhotoRef by viewModel.profilePhotoRef.collectAsStateWithLifecycle()
    var image by remember { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(key1 = profilePhotoRef) {
        profilePhotoRef.let {
            Glide.with(context)
                .load(it.first)
                .signature(ObjectKey(it.second?.updatedTimeMillis.toString()))
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        image = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
//                        image = null
                    }
                })
        }
    }

    var tempPhotoUri by remember { mutableStateOf(value = Uri.EMPTY) }

    val cameraLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccessful ->
        if (isSuccessful) tempPhotoUri.path?.let { _ -> viewModel.onSaveNewPicture(tempPhotoUri) }
    }


    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        onPermissionResult = { granted ->
            if (granted) {
                tempPhotoUri = context.createTempPictureUri()
                cameraLauncher.launch(tempPhotoUri)
            } else print("camera permission is denied")
        }
    )

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 0.dp)
            .verticalScroll(columnState)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Image(
            painter = image?.let { rememberDrawablePainter(drawable = it) } ?: painterResource(viewModel.profile.logo),
            contentDescription = null,

            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable { cameraPermissionState.launchPermissionRequest() },
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = viewModel.profile.fullName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(all = 0.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .width(320.dp)
                    .height(0.dp)
            )
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Job role", body = viewModel.profile.jobRole)
            InfoLine(
                modifier = modifier.padding(start = 15.dp),
                title = "Department",
                body = viewModel.profile.department +
                        if (viewModel.profile.subDepartment.isEmpty()) EmptyString.str else "/${viewModel.profile.subDepartment}"
            )
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Company", body = viewModel.profile.company)
            InfoLine(modifier = modifier.padding(start = 15.dp), title = "Email", body = viewModel.profile.email)
            InfoLine(
                modifier = modifier.padding(start = 15.dp),
                title = "Phone number",
                body = viewModel.profile.phoneNumber ?: "-"
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        RecordActionTextBtn(
            text = "Logout",
            onClick = { viewModel.logout() },
            colors = Pair(ButtonDefaults.textButtonColors(), MaterialTheme.colorScheme.primary)
        )
        RecordActionTextBtn(
            text = "Edit user data",
            onClick = onEditUserData,
            colors = Pair(ButtonDefaults.textButtonColors(), MaterialTheme.colorScheme.primary)
        )
        RecordActionTextBtn(
            text = "Delete account",
            onClick = { viewModel.showActionApproveDialog() },
            colors = Pair(
                ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                ),
                MaterialTheme.colorScheme.onErrorContainer
            )
        )
    }

    if (approveActionDialogVisibility) {
        ApproveAction(
            msg = "Are you sure you want to delete your account: ${viewModel.profile.email}?",
            onCanselClick = { onDenyLambda() },
            onOkClick = { password -> onApproveLambda(password) }
        )
    }
}

fun Context.createTempPictureUri(
    provider: String = "${BuildConfig.APPLICATION_ID}.provider",
    fileName: String = "picture_${System.currentTimeMillis()}",
    fileExtension: String = ".png"
): Uri {
    val tempFile = File.createTempFile(
        fileName, fileExtension, cacheDir
    ).apply {
        createNewFile()
    }

    return FileProvider.getUriForFile(applicationContext, provider, tempFile)
}

@Preview(name = "Lite Mode Settings", showBackground = true, widthDp = 360)
@Composable
fun SettingsPreview() {
    QMAppTheme {
        Settings(
            viewModel = hiltViewModel(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 0.dp),
            onLogOut = {},
            onEditUserData = {}
        )
    }
}
