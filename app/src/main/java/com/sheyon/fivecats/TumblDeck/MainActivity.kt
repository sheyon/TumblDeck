package com.sheyon.fivecats.TumblDeck

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebViewClient
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.widget.Button
import com.sheyon.fivecats.TumblDeck.R.id.recyclerView
import com.sheyon.fivecats.TumblDeck.R.id.webView

import com.sheyon.fivecats.TumblDeck.data.Login
import com.sheyon.fivecats.TumblDeck.data.Retriever
import com.sheyon.fivecats.TumblDeck.viewmodels.PhotoScreenViewModel

import com.tumblr.jumblr.JumblrClient
import com.tumblr.jumblr.types.Post

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var context : Context
    lateinit var recyclerView: RecyclerView
    lateinit var reloadButton: Button
    lateinit var sViewModel: PhotoScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sViewModel = ViewModelProviders.of(this).get(PhotoScreenViewModel::class.java)

        context = this
        reloadButton = findViewById(R.id.ReloadButton)
        reloadButton.setOnClickListener({
            JumblrTest()
        })

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
        if (sViewModel.posts != null && !sViewModel.posts!!.isEmpty()) {
            setAdapter(sViewModel.posts!!)
            Log.d("DEBUG", "Old Posts Found: " + sViewModel.posts)
        } else {
            jumblrAsyncTask().execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class jumblrAsyncTask : AsyncTask<Void, Void, List<Post>>() {
        override fun doInBackground(vararg params: Void): List<Post>? {
            val jumblrClient = JumblrClient(Retriever().primaryRetriever(), Retriever().secondaryRetriever())
            jumblrClient.setToken(prefs.accessToken, prefs.accessTokenSecret)

            val params = HashMap<String, Any>()
            params.put("type", "photo")
            if (sViewModel.posts != null && !sViewModel.posts!!.isEmpty()) {
                params.put("since_id", sViewModel.posts!!.first().id)
            }

            val posts : List<Post> = jumblrClient.userDashboard(params)
            sViewModel.posts = posts

            return posts
        }

        override fun onPostExecute(posts: List<Post>) {
            setAdapter(posts)
        }
    }

    fun setAdapter(posts : List<Post>) {
        webView.setVisibility(View.GONE)

        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val noOfColumns = (dpWidth / 180).toInt()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = StaggeredGridLayoutManager(noOfColumns, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setAdapter(PhotoAdapter(posts, context))
    }


}