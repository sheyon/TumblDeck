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
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth10aService

import com.sheyon.fivecats.TumblDeck.MainActivity
import com.sheyon.fivecats.TumblDeck.prefs
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class Login constructor(val activity: MainActivity) {

    private val consumerKey = Retriever().primaryRetriever()
    private val consumerSecret = Retriever().secondaryRetriever()
    private val callbackURL = Retriever().callbackRetriever()
    private val service: OAuth10aService = ServiceBuilder(consumerKey).apiSecret(consumerSecret).callback(callbackURL).build(TumblrApi.instance())

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

            Log.d ("DEBUG", "Credentials found")
            Log.d ("DEBUG", "OAuth Verifier: "+ prefs.accessVerifier)
            Log.d ("DEBUG", "Access Token: " + prefs.accessToken)
            Log.d ("DEBUG", "Access Token Secret: " + prefs.accessTokenSecret)

            GetWelcomeAsyncTask().execute()
        }
        else {
            Log.d ("DEBUG", "No credentials found. Requesting new token...")
            GetRequestTokenAsyncTask().execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetWelcomeAsyncTask : AsyncTask<Void, Void, String>() {

        private val PROTECTED_RESOURCE_URL = "http://api.tumblr.com/v2/user/info"

        override fun doInBackground(vararg params: Void?): String {

            val request = OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL)
            service.signRequest(accessToken, request)
            val response = service.execute(request)

            val obj = JSONObject(response.body)
            val name = obj.getJSONObject("response").getJSONObject("user").getString("name")

            return name
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            activity.textView.setText("Welcome " + result + "!")
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetRequestTokenAsyncTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                // Obtain the Request Token
                requestToken = service.requestToken
                val url = service.getAuthorizationUrl(requestToken)

                setupWebViewClient(url)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
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
        activity.setupWebView(webViewClient, url)
    }

    fun finishAuthentication(verifier: String) {
        prefs.accessVerifier = verifier
        Log.d ("DEBUG", "OAuth Verifier: "+ verifier)
        Log.d ("DEBUG", "Verifier confirmed. Requesting access token...")
        GetAccessTokenAsyncTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetAccessTokenAsyncTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void?): Void? {
            accessToken = service.getAccessToken(requestToken, oauthVerifier)

            prefs.accessToken = accessToken.token
            prefs.accessTokenSecret = accessToken.tokenSecret

            Log.d ("DEBUG", "Access Token: " + prefs.accessToken)
            Log.d ("DEBUG", "Access Token Secret: " + prefs.accessTokenSecret)

            return null
        }

        override fun onPostExecute(result: Void?) {
            GetWelcomeAsyncTask().execute()
        }
    }
}