package com.sheyon.fivecats.TumblDeck

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.sheyon.fivecats.TumblDeck.data.Login
import com.sheyon.fivecats.TumblDeck.data.Retriever
import com.sheyon.fivecats.TumblDeck.viewmodels.PhotoScreenViewModel
import com.sheyon.fivecats.TumblDeck.views.TumblDeckContainer

import com.tumblr.jumblr.JumblrClient
import com.tumblr.jumblr.types.Post

class MainActivity : AppCompatActivity() {

    private lateinit var container : TumblDeckContainer
    private lateinit var textView : TextView

    lateinit var context : Context
    lateinit var jumblrClient : JumblrClient
    lateinit var viewModel : PhotoScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this
        container = getContainer()
        viewModel = ViewModelProviders.of(this).get(PhotoScreenViewModel::class.java)

        setupViews()
    }

    fun setupViews() {
        textView = findViewById(R.id.textView)

        //LOGIN BUTTON
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            if (TumblDeckApp().isInternetConnected(context)) {
                Login(this).checkCredentials()
            } else {
                //TODO: Handle layout and behaviors for no internet
                textView.setText("You cannot connect!")
            }
        }

        //LOGOUT BUTTON
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            prefs.accessToken = ""
            prefs.accessTokenSecret = ""
            prefs.accessVerifier = ""
            Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show()
        }

        //GET PHOTOS BUTTON
        findViewById<Button>(R.id.getPhotosButton).setOnClickListener {
            setTokens()
            GetPhotos().execute()
        }

        if (!TumblDeckApp().isInternetConnected(context)) {
            textView.setText("You are not connected!")
        }
    }

    fun getContainer() : TumblDeckContainer {
        container = findViewById(R.id.container)
        return container
    }

    fun JumblrTest() {
        setTokens()
        GetWelcome().execute()

        if (viewModel.posts != null && !viewModel.posts!!.isEmpty()) {
            setAdapter(viewModel.posts!!)
            Log.d("DEBUG", "Old Posts Found: " + viewModel.posts)
        }
    }

    fun setTokens() {
        Log.d ("DEBUG", "Initializing Jumblr client")
        jumblrClient = JumblrClient(Retriever().primaryRetriever(), Retriever().secondaryRetriever())
        jumblrClient.setToken(prefs.accessToken, prefs.accessTokenSecret)
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetWelcome: AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            val name = jumblrClient.user().name
            return name
        }

        override fun onPostExecute(name: String?) {
            textView.setText("Welcome, " + name + "!")
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetPhotos: AsyncTask<Void, Void, List<Post>>() {
        override fun doInBackground(vararg params: Void): List<Post>? {
            val params = HashMap<String, Any>()
            params.put("type", "photo")

            val posts : List<Post> = jumblrClient.userDashboard(params)
            viewModel.posts = posts

            return posts
        }

        override fun onPostExecute(posts: List<Post>) {
            swapContainer(R.layout.container_photos)
            setAdapter(posts)
        }
    }

    fun swapContainer(layout: Int) {
        container.removeAllViews()
        setContentView(LayoutInflater.from(context).inflate(layout, container))
    }

    fun setAdapter(posts : List<Post>) {
        val photoGrid = container.findViewById<RecyclerView>(R.id.photoGridRecyclerView)
        photoGrid.setAdapter(PhotoAdapter(posts, context))
    }

}