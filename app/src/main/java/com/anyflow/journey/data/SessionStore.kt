package com.anyflow.journey.data

import android.content.Context
import android.content.SharedPreferences

/** Sesi login (Sanctum token + identitas ringkas) di SharedPreferences. */
class SessionStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("journey_session", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var name: String?
        get() = prefs.getString(KEY_NAME, null)
        set(value) = prefs.edit().putString(KEY_NAME, value).apply()

    var initials: String?
        get() = prefs.getString(KEY_INITIALS, null)
        set(value) = prefs.edit().putString(KEY_INITIALS, value).apply()

    var community: String?
        get() = prefs.getString(KEY_COMMUNITY, null)
        set(value) = prefs.edit().putString(KEY_COMMUNITY, value).apply()

    fun isLoggedIn(): Boolean = !token.isNullOrBlank()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
        private const val KEY_INITIALS = "initials"
        private const val KEY_COMMUNITY = "community"
    }
}
