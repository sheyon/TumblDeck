package com.sheyon.fivecats.TumblDeck

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

import com.sheyon.fivecats.TumblDeck.data.TumblDeckPreferences

class TumblDeckApp : Application() {

    companion object {
        var prefs: TumblDeckPreferences? = null
    }

    override fun onCreate() {
        super.onCreate()
        prefs = TumblDeckPreferences(applicationContext)
    }

    fun isConnected(context: Context): Boolean {
        var network = false
        try {
            val cm : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork : NetworkInfo = cm.activeNetworkInfo
            network = activeNetwork.isConnectedOrConnecting
        }
        catch (e : Exception) {
            e.printStackTrace()
        }

        return network
    }
}