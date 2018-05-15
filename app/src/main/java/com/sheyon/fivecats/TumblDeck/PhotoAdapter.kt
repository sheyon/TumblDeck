package com.sheyon.fivecats.TumblDeck

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.squareup.picasso.Picasso
import com.tumblr.jumblr.types.PhotoPost
import com.tumblr.jumblr.types.Post
import kotlinx.android.synthetic.main.picture_grid.view.*

class PhotoAdapter(val posts: List<Post>, val context: Context) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val pictureGridPhoto : ImageView = view.photoView
        val pictureGridLabel : TextView = view.photoPostedBy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.picture_grid, parent, false))
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoPost = posts[position] as PhotoPost
        val listPhotos = photoPost.photos
        val listSizes = listPhotos[0].sizes
        val penultIndex = listSizes.lastIndex - 2
        val url = listSizes[penultIndex].url

        Picasso.get()
                .load(url)
                .resize(250, 250)
                .centerCrop()
                .into(holder.pictureGridPhoto)
        holder.pictureGridLabel.setText(photoPost.blogName)
    }

}