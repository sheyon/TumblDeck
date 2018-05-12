package com.sheyon.fivecats.TumblDeck

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebViewClient

import com.sheyon.fivecats.TumblDeck.data.Login
import com.sheyon.fivecats.TumblDeck.data.Retriever
import com.tumblr.jumblr.JumblrClient
import com.tumblr.jumblr.types.Post

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var context : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        if (TumblDeckApp().isInternetConnected(context)) {
            Login(this).checkCredentials()
        } else {
            //TODO: Handle layout and behaviors for no internet
            textView.setText("You are not connected!")
        }
    }

    fun setupWebView(webViewClient: WebViewClient, url: String) {
        webView.setVisibility(View.VISIBLE)
        webView.post {
            webView.webViewClient = webViewClient
            webView.settings.domStorageEnabled = true
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(url)
        }
    }

    fun JumblrTest() {
        jumblrAsyncTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class jumblrAsyncTask : AsyncTask<Void, Void, PhotoAdapter>() {
        override fun doInBackground(vararg params: Void): PhotoAdapter {
            val jumblrClient = JumblrClient(Retriever().primaryRetriever(), Retriever().secondaryRetriever())
            jumblrClient.setToken(prefs.accessToken, prefs.accessTokenSecret)

            val params = HashMap<String, Any>()
            params.put("type", "photo")

            val posts : List<Post> = jumblrClient.userDashboard(params)
            val adapter = PhotoAdapter(context, R.layout.picture_grid, posts)

            return adapter
        }

        override fun onPostExecute(adapter: PhotoAdapter) {
            webView.setVisibility(View.GONE)
            gridView.setAdapter(adapter)
        }
    }

}