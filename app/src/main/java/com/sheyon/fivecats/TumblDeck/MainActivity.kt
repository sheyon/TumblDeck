package com.sheyon.fivecats.TumblDeck

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sheyon.fivecats.TumblDeck.data.Login

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val login = Login(this)
        login.runNetworkAsyncTask(login)
    }

    fun setupWebView(webViewClient: WebViewClient, url: String) {
        //This is called in the first stage of the login process
        val webView: WebView = findViewById(R.id.webView)

        webView.post {
            webView.webViewClient = webViewClient
            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
        }
    }
}