package com.jay.ktor_data_exchange.app_b.ui

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jay.ktor_data_exchange.app_b.client.ClientController
import kotlinx.coroutines.launch
import java.io.InputStream

class AppBViewModel(
    private val clientController: ClientController
) : ViewModel() {

    fun sendImage(context: Context, uri: Uri, ip: String, port: Int, token: String) {
        viewModelScope.launch {
            val base64 = uri.toBase64(context)
            clientController.sendDataToServer(ip, port, token, base64)
        }
    }

    private fun Uri.toBase64(context: Context): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(this)
        val bytes = inputStream?.readBytes() ?: return ""
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}
