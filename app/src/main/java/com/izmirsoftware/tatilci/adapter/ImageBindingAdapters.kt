package com.izmirsoftware.tatilci.adapter

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.izmirsoftware.tatilci.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

//Bu üç metodu adaptör içinde databinding ile resimleri yüklemek için ekledik
@BindingAdapter("downloadImage")
fun downloadImage(view: ImageView, url: String?) {
    view.imageDownload(url, view.context)
}

fun ImageView.imageDownload(url: String?, context: Context) {
    val options = RequestOptions()
            .placeholder(createPlaceholder(context))
            .error(R.drawable.placeholder)

    Glide.with(context).setDefaultRequestOptions(options).load(url).into(this)
}

fun createPlaceholder(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}