package com.simenko.qmapp.presentation.ui.main.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.simenko.qmapp.other.Constants
import com.simenko.qmapp.data.repository.UserRepository
import com.simenko.qmapp.data.repository.UserState
import com.simenko.qmapp.data.cache.prefs.model.Principal
import com.simenko.qmapp.domain.usecase.ClearDbUseCase
import com.simenko.qmapp.presentation.ui.main.main.MainPageHandler
import com.simenko.qmapp.presentation.ui.main.main.MainPageState
import com.simenko.qmapp.presentation.ui.main.main.content.Page
import com.simenko.qmapp.navigation.AppNavigator
import com.simenko.qmapp.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appNavigator: AppNavigator,
    private val mainPageState: MainPageState,
    private val userRepository: UserRepository,
    private val remoteConfig: FirebaseRemoteConfig,
    private val firebaseAuth: FirebaseAuth,
    private val clearDbUseCase: ClearDbUseCase
) : ViewModel() {
    /**
     * Main page setup -------------------------------------------------------------------------------------------------------------------------------
     * */
    val mainPageHandler: MainPageHandler
    private val _profilePhotoRef = MutableStateFlow<Pair<StorageReference?, StorageMetadata?>>(Pair(null, null))
    val profilePhotoRef = _profilePhotoRef.asStateFlow()

    fun onEntered() {
        firebaseAuth.signInWithEmailAndPassword(profile.email, profile.password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.user?.uid?.let { uid ->
                    val ref = Firebase.storage("gs://${Constants.USER_PROFILE_PICTURE_BUCKET_NAME}").reference.child(uid)
                    ref.metadata.addOnCompleteListener {
                        if (it.isSuccessful) {
                            _profilePhotoRef.value = Pair(ref, it.result)
                        }
                    }
                }
            }
        }
    }

    fun onSaveNewPicture(picture: Uri) {
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.currentUser?.uid?.let { uid ->
                uploadProfilePicture(picture, uid)
            }
        } else {
            firebaseAuth.signInWithEmailAndPassword(profile.email, profile.password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.uid?.let { uid ->
                        uploadProfilePicture(picture, uid)
                    }
                }
            }
        }
    }

    private fun uploadProfilePicture(picture: Uri, pictureName: String) {
        val storageRef = Firebase.storage("gs://${Constants.USER_PROFILE_PICTURE_BUCKET_NAME}").reference
        val mountainsRef = storageRef.child(pictureName)
        mainPageHandler.updateLoadingState(Triple(true, false, null))
        mountainsRef.putFile(picture).addOnCompleteListener {
            if (it.isSuccessful) {
                mainPageHandler.updateLoadingState(Triple(false, false, null))
                _profilePhotoRef.value = Pair(it.result.storage, it.result.metadata)
            } else {
                val errorMsg = it.result.error?.message
                mainPageHandler.updateLoadingState(Triple(false, false, errorMsg))
            }
        }
    }


    init {
        mainPageHandler = MainPageHandler.Builder(Page.ACCOUNT_SETTINGS, mainPageState)
            .setOnPullRefreshAction { this.updateUserData() }
            .build()
        val restApiUrl = remoteConfig.getString("app_rest_api_url")
        println("SettingsViewModel remote config value: ${restApiUrl}, chars: ${restApiUrl.length}")
    }

    /**
     * Navigation ------------------------------------------------------------------------------------------------------------------------------------
     * */
    fun onUserDataEditClick() {
        appNavigator.tryNavigateTo(Route.Main.Settings.EditUserDetails(true))
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val _isApproveActionVisible = MutableStateFlow(false)
    val isApproveActionVisible: StateFlow<Boolean> = _isApproveActionVisible
    fun hideActionApproveDialog() {
        _isApproveActionVisible.value = false
    }

    fun showActionApproveDialog() {
        _isApproveActionVisible.value = true
    }

    val userState: StateFlow<UserState> get() = userRepository.userState
    val profile: Principal get() = userRepository.profile

    fun clearLoadingState(error: String? = null) {
        mainPageHandler.updateLoadingState(Triple(false, false, error))
        userRepository.clearErrorMessage()
    }

    fun logout() {
        mainPageHandler.updateLoadingState(Triple(true, false, null))
        userRepository.logout()
    }

    fun deleteAccount(userEmail: String, password: String) {
        mainPageHandler.updateLoadingState(Triple(true, false, null))
        userRepository.deleteAccount(userEmail, password)
    }

    fun onLogOut() {
        viewModelScope.launch {
            clearDbUseCase.execute()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun updateUserData() {
        mainPageHandler.updateLoadingState(Triple(true, false, null))
        userRepository.updateUserData()
    }
}