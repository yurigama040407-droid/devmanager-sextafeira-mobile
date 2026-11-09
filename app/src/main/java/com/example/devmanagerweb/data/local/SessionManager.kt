package com.example.devmanagerweb.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.security.crypto.MasterKeys

import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "devmanager_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        const val KEY_USER_ID = "USER_ID"
        const val KEY_USER_PROFILE = "USER_PROFILE"
        const val KEY_AUTH_TOKEN = "AUTH_TOKEN"
    }

    fun saveUserId(id: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, id).apply()
    }

    fun fetchUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun saveUserProfile(profile: String) {
        sharedPreferences.edit().putString(KEY_USER_PROFILE, profile).apply()
    }

    fun fetchUserProfile(): String? {
        return sharedPreferences.getString(KEY_USER_PROFILE, null)
    }

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun fetchAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
