package com.example.chronicle.utils

import android.content.Context

/**
 * used sharedPreference to record whether the privacy policy is accepted
 * **/
object PreferencesUtil {
    private const val PREFERENCES_FILE_KEY = "com.example.android.privacy-policy"
    private const val PRIVACY_POLICY_ACCEPTED = "privacy_policy_accepted"

    fun hasAcceptedPrivacyPolicy(context: Context): Boolean {
        val preferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        return preferences.getBoolean(PRIVACY_POLICY_ACCEPTED, false)
    }

    fun acceptPrivacyPolicy(context: Context) {
        val preferences = context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
        with(preferences.edit()) {
            putBoolean(PRIVACY_POLICY_ACCEPTED, true)
            apply()
        }
    }
}
