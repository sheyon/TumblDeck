package com.sheyon.fivecats.TumblDeck

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebViewClient

import com.sheyon.fivecats.TumblDeck.data.Login
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context : Context = this

        if (TumblDeckApp().isInternetConnected(context)) {
            Login(this).checkCredentials()
        } else {
            //TODO: Handle layout and behaviors for no internet
            textView.setText("You are not connected!")
        }
    }

    fun setupWebView(webViewClient: WebViewClient, url: String) {

        webView.post {
            webView.webViewClient = webViewClient
            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
        }
    }
}