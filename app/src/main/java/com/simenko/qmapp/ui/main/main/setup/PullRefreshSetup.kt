package com.simenko.qmapp.ui.main.main.setup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PullRefreshSetup(val refreshAction: (() -> Unit)? = null) {
    private val _isSubLoadingInProgress = MutableStateFlow(false)
    val isSubLoadingInProgress: StateFlow<Boolean> get() = _isSubLoadingInProgress
    private val _isLoadingInProgress = MutableStateFlow(false)
    val isLoadingInProgress: StateFlow<Boolean> get() = _isLoadingInProgress
    private val _isErrorMessage = MutableStateFlow<String?>(null)
    val isErrorMessage: StateFlow<String?> get() = _isErrorMessage

    fun updateLoadingState(state: Triple<Boolean, Boolean, String?>) {
        _isSubLoadingInProgress.value = state.first
        _isLoadingInProgress.value = state.second
        _isErrorMessage.value = state.third
    }

    fun onNetworkErrorShown() {
        _isSubLoadingInProgress.value = false
        _isLoadingInProgress.value = false
        _isErrorMessage.value = null
    }
}