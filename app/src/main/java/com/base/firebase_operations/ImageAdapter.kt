package com.base.firebase_operations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class ImageAdapter(val context: Context, val uri:List<String>):RecyclerView.Adapter<ImageAdapter.imageviewholder>(){

    class imageviewholder(itemview: View):RecyclerView.ViewHolder(itemview){
      val imageholder=itemview.findViewById<ImageView>(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): imageviewholder {
        val view=LayoutInflater.from(context).inflate(R.layout.imageitem,parent,false)
        return imageviewholder(view)
    }

    override fun getItemCount(): Int {
        return uri.size
    }

    override fun onBindViewHolder(holder: imageviewholder, position: Int) {
        val currenturl=uri[position]
        Glide.with(context).load(currenturl).into(holder.imageholder)
    }

}
