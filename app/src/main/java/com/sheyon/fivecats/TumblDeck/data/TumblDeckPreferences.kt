package com.sheyon.fivecats.TumblDeck.data

import android.content.Context
import android.content.SharedPreferences

class TumblDeckPreferences (context : Context){

    val PREFS_FILENAME = "com.sheyon.fivecats.TumblDeck.prefs"
    val prefs : SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    val ACCESS_TOKEN = "oauth_token"
    val AUTHENTICATED_URL = "authenticated_url"

    var accessToken: String
        get() = prefs.getString(ACCESS_TOKEN, "")
        set(value) = prefs.edit().putString(ACCESS_TOKEN, value).apply()

    var loginUrl: String
        get() = prefs.getString(AUTHENTICATED_URL, "")
        set(value) = prefs.edit().putString(AUTHENTICATED_URL, value).apply()

}