package com.base.firebase_operations

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity2 : AppCompatActivity() {

    private val Requestcode=0
    private var currentfile: Uri?=null
    lateinit var imageholder:ImageView
    val imagereff=Firebase.storage.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        imageholder=findViewById(R.id.viewimage)
        val upload: Button=findViewById(R.id.button5)
        imageholder.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type="image/*"
                startActivityForResult(it,Requestcode)
            }
        }
        upload.setOnClickListener {
            UploadImagetoCloud("TestImages")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && requestCode==Requestcode){
            data?.data?.let{
                currentfile=it
                imageholder.setImageURI(it)
            }
        }
    }

    private fun UploadImagetoCloud(filename:String)= CoroutineScope(Dispatchers.IO).launch {

        try{
            currentfile?.let {
                imagereff.child("images/$filename").putFile(it).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity2,"Image uploaded Successfully",Toast.LENGTH_LONG).show()
                }
            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity2,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }
}