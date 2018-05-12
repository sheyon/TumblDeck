package com.sheyon.fivecats.TumblDeck

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

import com.squareup.picasso.Picasso
import com.tumblr.jumblr.types.PhotoPost
import com.tumblr.jumblr.types.Post

class PhotoAdapter constructor(context: Context, resource: Int, post: List<Post>) : ArrayAdapter<Post>(context, resource, post)  {

    val sContext = context
    val sResource = resource
    val sPosts = post

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        lateinit var viewHolder: ViewHolder
        lateinit var newView: View

        val photoPost = sPosts[position] as PhotoPost
        val listPhotos = photoPost.photos
        val listSizes = listPhotos[0].sizes
        val penultIndex = listSizes.lastIndex - 2
        val url = listSizes[penultIndex].url

        if (convertView == null) {
            newView = LayoutInflater.from(sContext).inflate(sResource, parent, false)
        }
        else {
            newView = convertView
        }

        viewHolder = ViewHolder()
        viewHolder.image = newView.findViewById(R.id.photoView) as ImageView?
        Picasso.get().load(url).into(viewHolder.image)

        return newView
    }

    internal class ViewHolder {
        var image: ImageView? = null
    }
}