package com.jay.ktor_data_exchange.app_a.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.Observer
import com.jay.ktor_data_exchange.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppAActivity : ComponentActivity() {

    private val viewModel: AppAViewModel by viewModel()

    private lateinit var qrText: TextView
    private lateinit var qrImageView: ImageView
    private lateinit var receivedImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_aactivity)

        qrText = findViewById(R.id.qrText)
        qrImageView = findViewById(R.id.qrImageView)
        receivedImage = findViewById(R.id.receivedImage)

        findViewById<Button>(R.id.startServerBtn).setOnClickListener {
            viewModel.startServer()
        }

        viewModel.connectionInfo.observe(this, Observer { info ->
            qrText.text = info
            val bitmap = generateQrCode(info)
            qrImageView.setImageBitmap(bitmap)
        })

        viewModel.receivedBitmap.observe(this, Observer { bitmap ->
            receivedImage.setImageBitmap(bitmap)
        })
    }

    private fun generateQrCode(text: String): Bitmap {
        val writer = com.google.zxing.qrcode.QRCodeWriter()
        val bitMatrix = writer.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, 512, 512)
        val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bmp
    }
}
