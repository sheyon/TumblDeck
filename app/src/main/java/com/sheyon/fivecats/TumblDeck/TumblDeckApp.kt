package com.sheyon.fivecats.TumblDeck

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

import com.sheyon.fivecats.TumblDeck.data.TumblDeckPreferences

val prefs : TumblDeckPreferences by lazy {
    TumblDeckApp.prefs!!
}

class TumblDeckApp : Application() {

    companion object {
        var prefs: TumblDeckPreferences? = null
    }

    override fun onCreate() {
        prefs = TumblDeckPreferences(applicationContext)
        super.onCreate()
    }

    fun isInternetConnected(context: Context): Boolean {
        var connected = false
        try {
            val cm : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork : NetworkInfo = cm.activeNetworkInfo
            connected = activeNetwork.isConnectedOrConnecting
        }
        catch (e : Exception) {
            e.printStackTrace()
        }
        return connected
    }

}