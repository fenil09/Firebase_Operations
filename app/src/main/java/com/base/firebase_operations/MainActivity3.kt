package com.base.firebase_operations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity3 : AppCompatActivity() {
    lateinit var recyclerview:RecyclerView
    val imagereff=Firebase.storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        recyclerview=findViewById(R.id.recycler1)
        getfilefromCloud()
    }

    private fun getfilefromCloud()= CoroutineScope(Dispatchers.IO).launch {

        try{
            val imagesfromcloud=imagereff.child("images/").listAll().await()
            val imageurl= mutableListOf<String>()
            for(i in imagesfromcloud.items){
                val url=i.downloadUrl.await()
                imageurl.add(url.toString())
            }
            withContext(Dispatchers.Main){
                val cloudadapter=ImageAdapter(this@MainActivity3,imageurl)
                recyclerview.apply {
                    adapter=cloudadapter
                    layoutManager=LinearLayoutManager(this@MainActivity3)
                }
            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity3,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }
}