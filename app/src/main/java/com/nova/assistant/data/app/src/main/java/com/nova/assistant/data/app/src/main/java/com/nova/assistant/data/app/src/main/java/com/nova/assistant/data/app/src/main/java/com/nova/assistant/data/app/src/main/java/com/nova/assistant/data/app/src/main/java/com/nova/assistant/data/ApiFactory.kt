
package com.nova.assistant.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiFactory {

    fun create(baseUrl: String): NovaApi {
        require(
            baseUrl.startsWith("https://") ||
            baseUrl.startsWith("http://10.0.2.2:")
        ) {
            "Use a secure HTTPS backend URL"
        }

        val normalizedUrl = baseUrl.trimEnd('/') + "/"

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NovaApi::class.java)
    }
}
