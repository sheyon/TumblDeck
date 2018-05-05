package com.sheyon.fivecats.TumblDeck

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.sheyon.fivecats.TumblDeck.TumblDeckApp.Companion.prefs
import com.sheyon.fivecats.TumblDeck.data.Login

class MainActivity : AppCompatActivity() {

    val OAUTH_VERIFIER_PARAM : String = "oauth_verifier"
    var oauthVerifier : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val context : Context = this

        if (TumblDeckApp().isConnected(context)) {
            if (prefs!!.loginUrl.isEmpty()) {
                Login(this).runNetworkAsyncTask()
            } else {
                setupWebViewClient(prefs!!.loginUrl)
            }
        }
        else {
            val textView : TextView = findViewById(R.id.textView)
            textView.setText("You are not connected!")
        }
    }

    //There are two entry points into this function
    //One should be the initial login. The app will check in OnCreate if there is already a verifier token available in preferences.
    //The other will request access tokens from scratch via NetworkAsyncTask
    fun setupWebViewClient(url: String) {

        val webViewClient = object : WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url

                if (uri.getQueryParameter(OAUTH_VERIFIER_PARAM) == null) {
                    return false
                } else {
                    oauthVerifier = uri.getQueryParameter(OAUTH_VERIFIER_PARAM)
                    //Login.GetAccessTokenAsyncTask().execute()
                }
                return false
            }

            @TargetApi(Build.VERSION_CODES.KITKAT)
            override fun shouldOverrideUrlLoading(view: WebView, verifierUrl: String): Boolean {
                Log.i("INFO", "Older OS detected!")
                val splitter = verifierUrl.split("=")

                if (!splitter[1].endsWith(OAUTH_VERIFIER_PARAM)) {
                    return false
                } else {
                    oauthVerifier = "*" + splitter[2]
                    oauthVerifier = oauthVerifier.removeSurrounding("*", "#_")
                    //Login.GetAccessTokenAsyncTask().execute()
                }
                return false
            }
        }
        setupWebView(webViewClient, url)
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