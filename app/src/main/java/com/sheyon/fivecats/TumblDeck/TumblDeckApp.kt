package com.sheyon.fivecats.TumblDeck

import android.app.Application
import com.sheyon.fivecats.TumblDeck.data.TumblDeckPreferences

class TumblDeckApp : Application() {
    companion object {
        var prefs: TumblDeckPreferences? = null
    }

    override fun onCreate() {
        super.onCreate()
        prefs = TumblDeckPreferences(applicationContext)
    }
}