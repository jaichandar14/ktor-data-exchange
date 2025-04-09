package com.jay.ktor_data_exchange

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.InputStream

class AppBMainActivity : AppCompatActivity() {

    private lateinit var qrLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private var ip: String = ""
    private var port: Int = 0
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_bmain)

        // Set up QR scanner
        qrLauncher = registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                val parts = result.contents.split("|")
                if (parts.size == 3) {
                    ip = parts[0]
                    port = parts[1].toIntOrNull() ?: 0
                    token = parts[2]
                    openImagePicker()
                }
            }
        }

        // Set up image picker
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    uri?.let {
                        val base64Image = getBase64FromUri(it)
                        lifecycleScope.launch {
                            sendDataToServer(ip, port, token, base64Image)
                        }
                    }
                }
            }

        findViewById<Button>(R.id.scanQrBtn).setOnClickListener {
            startQrScan()
        }
    }

    private fun startQrScan() {
        val options = ScanOptions().apply {
            setPrompt("Scan IP|Port|Token QR")
            setBeepEnabled(false)
            setOrientationLocked(true)
        }
        qrLauncher.launch(options)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    private fun getBase64FromUri(uri: Uri): String {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return ""
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private suspend fun sendDataToServer(
        ip: String,
        port: Int,
        token: String,
        base64Image: String
    ) {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        try {
            val response = client.post("http://$ip:$port/receive") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(base64Image)
            }
            println("Response status: ${response.status}")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }
    }
}
