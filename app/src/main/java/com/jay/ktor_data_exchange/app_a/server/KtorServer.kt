package com.jay.ktor_data_exchange.app_a.server

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.Inet4Address
import java.net.NetworkInterface

class KtorServer {

    val receivedBitmap = MutableLiveData<Bitmap>()
    val connectionInfo = MutableLiveData<String>()

    private val token = "my_secure_token"
    private val port = 8080

    fun getPort() = port
    fun getToken() = token

    fun getDeviceIpAddress(): String? {
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

    suspend fun startServer() {
        val ip = getDeviceIpAddress() ?: return
        val info = "$ip|$port|$token"
        connectionInfo.postValue(info)

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
                            receivedBitmap.value = bitmap
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

