package com.jay.ktor_data_exchange.app_b.client

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.InputStream

class ClientController {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun sendDataToServer(ip: String, port: Int, token: String, base64Image: String) {
        try {
            val response = client.post("http://$ip:$port/receive") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(base64Image)
            }
            println("Response: ${response.status}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
