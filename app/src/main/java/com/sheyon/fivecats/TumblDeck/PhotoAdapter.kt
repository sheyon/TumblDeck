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
import com.tumblr.jumblr.types.PhotoSize
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
        val photoPost = posts[position] as PhotoPost                                                //All photo posts in a payload
        val listPhotos = photoPost.photos                                                           //All photos in a post
        val listSizes = listPhotos[0].sizes                                                         //A list of the Tumblr-provided resizes of the [0] photo

        holderSetImageView(listSizes, holder)
        holder.pictureGridLabel.setText(photoPost.blogName)
    }

    fun holderSetImageView(listSizes: MutableList<PhotoSize>, holder: PhotoViewHolder) {
        //Normalize small images
        if (listSizes[0].width < 400) {
            Picasso.get()
                    .load(listSizes[0].url)
                    .resize(500, 500)
                    .centerCrop()
                    .into(holder.pictureGridPhoto)
        }
        //Else display them at 500-400px
        else {
            for (i in listSizes.indices) {
                if (listSizes[i].width <= 500) {
                    Picasso.get()
                            .load(listSizes[i].url)
                            .into(holder.pictureGridPhoto)
                    return
                }
            }
        }
    }


}