package com.jay.ktor_data_exchange.app_a.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.ktor_data_exchange.app_a.server.KtorServer
import kotlinx.coroutines.launch

class AppAViewModel(private val controller: KtorServer) : ViewModel() {

    val connectionInfo = controller.connectionInfo
    val receivedBitmap = controller.receivedBitmap

    fun startServer() {
        viewModelScope.launch {
            controller.startServer()
        }
    }
}
