package com.base.firebase_operations

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val carcollection= Firebase.firestore.collection("Cars")
    lateinit var view:TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val modelname:EditText=findViewById(R.id.et1)
        val colorname:EditText=findViewById(R.id.et2)
        val save: Button =findViewById(R.id.button)
        val retreive:Button=findViewById(R.id.carbutton)
        view=findViewById(R.id.textView)

        save.setOnClickListener {
            val name=modelname.text.toString()
            val color=colorname.text.toString()
            savecar(cars(name,color))
        }

        retreive.setOnClickListener {
            getCar()
        }
        getrealtimeupdates()
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

    private fun getCar() = CoroutineScope(Dispatchers.IO).launch {

        try{
            val querySnapshot=carcollection.get().await()
            val sb=StringBuilder()
            for(document in querySnapshot.documents){
                val car=document.toObject<cars>()
                sb.append("$car\n")
            }
            withContext(Dispatchers.Main){
                view.text=sb.toString()
            }
        }
        catch(e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message.toString(),Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun getrealtimeupdates(){
        carcollection.addSnapshotListener{querysnapshot,FirebaseFireStoreException ->
            FirebaseFireStoreException?.let {
                Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                return@addSnapshotListener
            }

            querysnapshot?.let {
                val sb=StringBuilder()
                for(document in querysnapshot.documents){
                    val car=document.toObject<cars>()
                    val name= car?.modelname
                    val color=car?.color
                    sb.append("$name\n$color\n")
                }
                view.text=sb.toString()
            }
        }
    }
}