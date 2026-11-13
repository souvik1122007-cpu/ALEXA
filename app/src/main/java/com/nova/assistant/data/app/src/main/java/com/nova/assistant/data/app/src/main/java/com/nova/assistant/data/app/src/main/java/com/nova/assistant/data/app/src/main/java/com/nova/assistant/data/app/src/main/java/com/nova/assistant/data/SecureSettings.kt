
package com.nova.assistant.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureSettings(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        "nova_secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var backendUrl: String
        get() = preferences.getString("backend_url", "") ?: ""
        set(value) {
            preferences.edit().putString("backend_url", value).apply()
        }

    var accessToken: String
        get() = preferences.getString("access_token", "") ?: ""
        set(value) {
            preferences.edit().putString("access_token", value).apply()
        }

    fun clear() {
        preferences.edit().clear().apply()
    }
}
