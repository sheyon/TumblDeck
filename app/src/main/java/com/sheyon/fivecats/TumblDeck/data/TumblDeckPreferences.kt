package com.sheyon.fivecats.TumblDeck.data

import android.content.Context
import android.content.SharedPreferences

class TumblDeckPreferences (context : Context){

    val PREFS_FILENAME = "com.sheyon.fivecats.TumblDeck.prefs"
    val prefs : SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val OAUTH_TOKEN = "oauth_token"
    val OAUTH_SECRET = "oauth_secret"

    var loginToken: String
        get() = prefs.getString(OAUTH_TOKEN, "")
        set(value) = prefs.edit().putString(OAUTH_TOKEN, value).apply()

    var loginSecret: String
        get() = prefs.getString(OAUTH_SECRET, "")
        set(value) = prefs.edit().putString(OAUTH_SECRET, value).apply()
}