package com.simenko.qmapp.ui.main.main.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PullRefreshSetup(val refreshAction: (() -> Unit)? = null) {
    private val _isLoadingInProgress = MutableStateFlow(false)
    val isLoadingInProgress: StateFlow<Boolean> get() = _isLoadingInProgress
    private val _isErrorMessage = MutableStateFlow<String?>(null)
    val isErrorMessage: StateFlow<String?> get() = _isErrorMessage

    fun updateLoadingState(state: Pair<Boolean, String?>) {
        _isLoadingInProgress.value = state.first
        _isErrorMessage.value = state.second
    }

    fun onNetworkErrorShown() {
        _isLoadingInProgress.value = false
        _isErrorMessage.value = null
    }
}