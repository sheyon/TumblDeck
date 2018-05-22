package com.sheyon.fivecats.TumblDeck.views

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewScreen(context: Context?, attrs: AttributeSet?) : WebView(context, attrs) {

    val webView = this

    fun setupWebView(webViewClient: WebViewClient, url: String) {
        webView.post {
            webView.webViewClient = webViewClient
            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
        }
    }

}