package com.example.takenotesapp

import android.net.Uri
import android.text.format.DateUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.takenotesapp.database.Note


@BindingAdapter("titleList")
fun TextView.setTitle(item: Note){
    text = item.title
}

@BindingAdapter("headerList")
fun TextView.setHeader(item: Note){
    text = item.header
}

@BindingAdapter("editedList")
fun TextView.setLastEdited(item: Note){
    text = DateUtils.getRelativeTimeSpanString(item.lastEdited)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?){
    imgUrl?.let {
        val imgUri = Uri.parse(imgUrl)
        if(imgUri == null){
            Glide.with(imgView.context)
                .load(R.drawable.ic_search_black_24dp)
                .placeholder(R.drawable.ic_search_black_24dp)
                .error(R.drawable.ic_search_black_24dp)
                .into(imgView)
        }
        else{
            Glide.with(imgView.context)
                .load(imgUri)
                .placeholder(R.drawable.ic_search_black_24dp)
                .error(R.drawable.ic_broken_image_black_24dp)
                .into(imgView)
        }

    }
}

