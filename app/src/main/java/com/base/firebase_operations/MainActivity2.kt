package com.base.firebase_operations

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
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
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        imageholder=findViewById(R.id.viewimage)
        val upload: Button=findViewById(R.id.button5)
        val download:Button=findViewById(R.id.button6)
        val delete:Button=findViewById(R.id.button7)
        val changeScreen:Button=findViewById(R.id.button8)
        imageholder.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type="image/*"
                startActivityForResult(it,Requestcode)
            }
        }
        upload.setOnClickListener {
            UploadImagetoCloud(System.currentTimeMillis().toString())
        }
        download.setOnClickListener {
            DownloadImage("TestImages")
        }
        delete.setOnClickListener {
            DeleteImage("TestImages")
        }
        changeScreen.setOnClickListener {
            val intent:Intent=Intent(this,MainActivity3::class.java)
            startActivity(intent)
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
    private fun DownloadImage(filename:String) = CoroutineScope(Dispatchers.IO).launch {
        try{
            val maxsizedownload=5L*1024*1024
            val bytes=imagereff.child("images/$filename").getBytes(maxsizedownload).await()
            val bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            withContext(Dispatchers.Main){
                imageholder.setImageBitmap(bitmap)
            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity2,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun DeleteImage(filename: String)= CoroutineScope(Dispatchers.IO).launch {

        try{
            imagereff.child("images/$filename").delete().await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity2,"Image deleted successfully",Toast.LENGTH_LONG).show()
            }
        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity2,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }
}