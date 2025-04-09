package com.jay.ktor_data_exchange

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.Inet4Address
import java.net.NetworkInterface

class AppAActivity : AppCompatActivity() {

    private val token = "my_secure_token"
    private val port = 8080

    private lateinit var qrText: TextView
    private lateinit var qrImageView: ImageView
    private lateinit var receivedImage: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_aactivity)

        qrText = findViewById(R.id.qrText)
        qrImageView = findViewById(R.id.qrImageView)
        receivedImage = findViewById(R.id.receivedImage)

        val startServerBtn = findViewById<Button>(R.id.startServerBtn)

        startServerBtn.setOnClickListener {
            lifecycleScope.launch {
                startServer()
            }
        }
    }

    private fun getDeviceIpAddress(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        for (intf in interfaces) {
            val addresses = intf.inetAddresses
            for (addr in addresses) {
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress
                }
            }
        }
        return null
    }

    private fun generateQrCode(text: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bmp
    }

    private suspend fun startServer() {
        val ip = getDeviceIpAddress() ?: return
        val connectionInfo = "$ip|$port|$token"
        qrText.text = connectionInfo

        // Show QR code
        val qrBitmap = generateQrCode(connectionInfo)
        withContext(Dispatchers.Main) {
            qrImageView.setImageBitmap(qrBitmap)
        }

        embeddedServer(CIO, host = ip, port = port) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            routing {
                post("/receive") {
                    val auth = call.request.header("Authorization")?.removePrefix("Bearer ")
                    if (auth == token) {
                        val imageBase64 = call.receive<String>()
                        withContext(Dispatchers.Main) {
                            val bytes = Base64.decode(imageBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            receivedImage.setImageBitmap(bitmap)
                        }
                        call.respond(HttpStatusCode.OK, "Image received")
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                    }
                }
            }
        }.start(wait = false)
    }
}
