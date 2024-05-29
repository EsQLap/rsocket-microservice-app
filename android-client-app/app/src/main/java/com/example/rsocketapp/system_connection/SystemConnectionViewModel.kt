package com.example.rsocketapp.system_connection

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SystemConnectionViewModel : ViewModel() {

    private val _systemConnectionState = MutableStateFlow(SystemConnectionState())
    val connectivityState = _systemConnectionState.asStateFlow()

    fun onNetworkAvailable() {
        _systemConnectionState.update { it.copy(isAvailable = true) }
    }

    fun onNetworkLost() {
        _systemConnectionState.update { it.copy(isAvailable = false) }
    }
}