package com.sheyon.fivecats.TumblDeck.data

import android.content.Context
import android.content.SharedPreferences

class TumblDeckPreferences (context : Context) {

    private val PREFS_FILENAME = "com.sheyon.fivecats.TumblDeck.prefs"
    private val prefs : SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    private val TOKEN = "oauth_token"
    private val TOKEN_SECRET = "oauth_token_secret"
    private val TOKEN_VERIFIER = "oauth_verifier"

    var accessToken: String
        get() = prefs.getString(TOKEN, "")
        set(value) = prefs.edit().putString(TOKEN, value).apply()

    var accessTokenSecret: String
        get() = prefs.getString(TOKEN_SECRET, "")
        set(value) = prefs.edit().putString(TOKEN_SECRET, value).apply()

    var accessVerifier: String
        get() = prefs.getString(TOKEN_VERIFIER, "")
        set(value) = prefs.edit().putString(TOKEN_VERIFIER, value).apply()
}