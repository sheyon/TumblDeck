package com.sheyon.fivecats.TumblDeck.data

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.AsyncTask
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

import com.github.scribejava.apis.TumblrApi
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.oauth.OAuth10aService

import com.sheyon.fivecats.TumblDeck.MainActivity
import com.sheyon.fivecats.TumblDeck.R
import com.sheyon.fivecats.TumblDeck.prefs
import com.sheyon.fivecats.TumblDeck.views.WebViewScreen

class Login constructor(val activity: MainActivity) {

    private val consumerKey = Retriever().primaryRetriever()
    private val consumerSecret = Retriever().secondaryRetriever()
    private val callbackURL = Retriever().callbackRetriever()
    private val service: OAuth10aService = ServiceBuilder(consumerKey)
            .apiSecret(consumerSecret)
            .callback(callbackURL)
            .build(TumblrApi.instance())

    lateinit var requestToken: OAuth1RequestToken
    lateinit var accessToken: OAuth1AccessToken

    private val OAUTH_VERIFIER = "oauth_verifier"
    var oauthVerifier: String = ""

    fun checkCredentials() {
        Log.d ("DEBUG", "Checking credentials...")
        if (!prefs.accessToken.isEmpty() &&
                !prefs.accessTokenSecret.isEmpty() &&
                !prefs.accessVerifier.isEmpty()) {
            accessToken = OAuth1AccessToken(prefs.accessToken, prefs.accessTokenSecret)
            oauthVerifier = prefs.accessVerifier

            activity.JumblrTest()
        }
        else {
            Log.d ("DEBUG", "No credentials found. Requesting new token...")
            GetRequestTokenAsyncTask().execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetRequestTokenAsyncTask : AsyncTask<Void, Void, String>() {
        lateinit var url : String

        override fun doInBackground(vararg params: Void?): String {
            try {
                // Obtain the Request Token
                requestToken = service.requestToken
                url = service.getAuthorizationUrl(requestToken)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return url
        }

        override fun onPostExecute(url: String) {
            super.onPostExecute(url)
            setupWebViewClient(url)
        }
    }

    fun setupWebViewClient(url: String) {

        val webViewClient = object : WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                Log.i("INFO", "Newer OS detected!")
                val uri = request.url

                if (uri.getQueryParameter(OAUTH_VERIFIER) == null) {
                    return false
                } else {
                    oauthVerifier = uri.getQueryParameter(OAUTH_VERIFIER)
                    finishAuthentication(oauthVerifier)
                }
                return false
            }

            @TargetApi(Build.VERSION_CODES.KITKAT)
            override fun shouldOverrideUrlLoading(view: WebView, verifierUrl: String): Boolean {
                Log.i("INFO", "Older OS detected!")
                val splitter = verifierUrl.split("=")

                if (!splitter[1].endsWith(OAUTH_VERIFIER)) {
                    return false
                } else {
                    oauthVerifier = "*" + splitter[2]
                    oauthVerifier = oauthVerifier.removeSurrounding("*", "#_")
                    finishAuthentication(oauthVerifier)
                }
                return false
            }
        }

        activity.swapContainer(R.layout.container_webview)
        val webViewScreen = activity.getContainer().findViewById<WebViewScreen>(R.id.webView)
        webViewScreen.setupWebView(webViewClient, url)
    }

    fun finishAuthentication(verifier: String) {
        activity.swapContainer(R.layout.activity_main)
        activity.setupViews()
        prefs.accessVerifier = verifier
        GetAccessTokenAsyncTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetAccessTokenAsyncTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            Log.d ("DEBUG", "Verifier confirmed. Requesting access token...")
            accessToken = service.getAccessToken(requestToken, oauthVerifier)

            prefs.accessToken = accessToken.token
            prefs.accessTokenSecret = accessToken.tokenSecret

            return null
        }

        override fun onPostExecute(result: Void?) {
            activity.JumblrTest()
        }
    }
}