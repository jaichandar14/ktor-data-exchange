package com.jay.ktor_data_exchange.app_b.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.jay.ktor_data_exchange.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppBMainActivity : AppCompatActivity() {

    private val viewModel: AppBViewModel by viewModel()

    private lateinit var qrLauncher: ActivityResultLauncher<ScanOptions>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private var ip: String = ""
    private var port: Int = 0
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_bmain)

        // Setup QR Code Scanner
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

        // Setup Image Picker
        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    uri?.let {
                        viewModel.sendImage(this, it, ip, port, token)
                    }
                }
            }

        // Scan QR Button
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
}
