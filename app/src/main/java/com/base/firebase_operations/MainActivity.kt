package com.base.firebase_operations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private val carcollection= Firebase.firestore.collection("Cars")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val modelname:EditText=findViewById(R.id.et1)
        val colorname:EditText=findViewById(R.id.et2)
        val save: Button =findViewById(R.id.button)
        save.setOnClickListener {
            val name=modelname.text.toString()
            val color=colorname.text.toString()
            savecar(cars(name,color))
        }

    }

    private fun savecar(cars: cars)= CoroutineScope(Dispatchers.IO).launch {

        try{
            carcollection.add(cars).await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,"Task is successfull",Toast.LENGTH_LONG).show()
            }

        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }
}