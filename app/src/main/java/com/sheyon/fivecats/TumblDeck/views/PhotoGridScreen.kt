package com.sheyon.fivecats.TumblDeck.views

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.widget.Button
import android.widget.LinearLayout

import com.sheyon.fivecats.TumblDeck.MainActivity
import com.sheyon.fivecats.TumblDeck.PhotoAdapter
import com.sheyon.fivecats.TumblDeck.R

import com.tumblr.jumblr.types.Post

class PhotoGridScreen (context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    lateinit var loadButton : Button
    lateinit var recyclerView : RecyclerView

    override fun onFinishInflate() {
        super.onFinishInflate()

        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val noOfColumns = (dpWidth / 180).toInt()

        recyclerView = findViewById(R.id.photoGridRecyclerView)
        recyclerView.layoutManager = StaggeredGridLayoutManager(noOfColumns, StaggeredGridLayoutManager.VERTICAL)
        setAdapter(posts = null, context = context)

        loadButton = findViewById(R.id.loadPhotosButton)
        loadButton.setOnClickListener({
            val activity = context as MainActivity
            activity.GetPhotos().execute()
        })
    }

    fun setAdapter(posts: List<Post>?, context: Context) {
        recyclerView.setAdapter(PhotoAdapter(posts, context))
    }

}