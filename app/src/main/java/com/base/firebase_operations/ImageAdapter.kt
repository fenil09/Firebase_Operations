package com.base.firebase_operations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext



class ImageAdapter(val context: Context, val uri:List<String>):RecyclerView.Adapter<ImageAdapter.imageviewholder>(){

    val imagereff=Firebase.storage.reference
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
        holder.imageholder.setOnClickListener {

            val name=imagereff.child("images/$currenturl").name
            val chararray:CharArray=name.toCharArray()
            val startindex=9
            val endIndex=21
            val newarray=chararray.copyOfRange(startindex,endIndex + 1)
            val testreference=newarray.joinToString("")
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    imagereff.child("images/$testreference").delete().await()
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"image deleted successfully",Toast.LENGTH_LONG).show()

                    }
                }catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,e.message,Toast.LENGTH_LONG).show()

                    }
                }
            }
        }
    }

}
