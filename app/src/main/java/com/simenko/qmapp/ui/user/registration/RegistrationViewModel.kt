/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simenko.qmapp.ui.user.registration

import androidx.lifecycle.ViewModel
import com.simenko.qmapp.other.Event
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.repository.UserState
import com.simenko.qmapp.storage.Principle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * RegistrationViewModel is the ViewModel that the Registration flow ([LoginActivity]
 * and fragments) uses to keep user's input data.
 *
 * @Inject tells Dagger how to provide instances of this type. Dagger also knows
 * that UserManager is a dependency.
 */

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    val userState : StateFlow<Event<UserState>>
        get() = userRepository.userState

    private var acceptedTCs: Boolean? = null
    private var principle: Principle? = null

    fun updateUserData(fullName: String, department: String, subDepartment: String, jobRole: String, email: String, password: String) {
        val user = userRepository.user
        user.fullName = fullName
        user.department = department
        user.subDepartment = subDepartment
        user.jobRole = jobRole
        user.email = email
        user.password = password

        principle = user
    }

    fun acceptTCs() {
        acceptedTCs = true
    }

    fun registerUser() {
        assert(principle != null)
        assert(acceptedTCs == true)

        userRepository.registerUser(principle!!)
    }

    private val _isUserExistDialogVisible = MutableStateFlow(false)
    val isUserExistDialogVisible: StateFlow<Boolean> = _isUserExistDialogVisible
    fun hideUserExistDialog() {
        _isUserExistDialogVisible.value = false
    }
    fun showUserExistDialog() {
        _isUserExistDialogVisible.value = true
    }

    fun setLocalEmptyUser() {
        userRepository.clearUserData()
    }
}
