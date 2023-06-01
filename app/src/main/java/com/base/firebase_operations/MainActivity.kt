package com.base.firebase_operations

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val carcollection= Firebase.firestore.collection("Cars")
    lateinit var view:TextView
    lateinit var search:EditText
    lateinit var modelname:EditText
    lateinit var colorname:EditText
    lateinit var newmodelname:EditText
    lateinit var newcolorname:EditText
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        modelname=findViewById(R.id.et1)
        colorname=findViewById(R.id.et2)
        newmodelname=findViewById(R.id.et4)
        newcolorname=findViewById(R.id.et7)
        val save: Button =findViewById(R.id.button2)
        val retreive:Button=findViewById(R.id.carbutton)
        val update:Button=findViewById(R.id.button)
        val delete:Button=findViewById(R.id.button3)
        val transaction:Button=findViewById(R.id.button4)
        search=findViewById(R.id.et3)
        view=findViewById(R.id.textView)

        save.setOnClickListener {
            val name=modelname.text.toString()
            val color=colorname.text.toString()
            savecar(cars(name,color))
        }

        retreive.setOnClickListener {
            getCar()
        }
        //getrealtimeupdates()
        update.setOnClickListener {
            val oldcar=getoldcar()
            val newcar=getnewcar()
            updatecar(oldcar, newcar)
        }
        delete.setOnClickListener {
            val car=getoldcar()
            DeleteCar(car)
        }

        transaction.setOnClickListener {
            Transaction("7i9TIKreD1iztUJynCjh")
        }
        view.setOnClickListener {
            val intent: Intent =Intent(this@MainActivity,MainActivity2::class.java)
            startActivity(intent)
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

    private fun getCar() = CoroutineScope(Dispatchers.IO).launch {
        val searchparameter=search.text.toString()

        try{
            val querySnapshot=carcollection.whereEqualTo("modelname",searchparameter).get().await()
            if(querySnapshot.documents.isNotEmpty()){
                val sb=StringBuilder()
                for(document in querySnapshot.documents){
                    val car=document.toObject<cars>()
                    sb.append("$car\n")
                }
                withContext(Dispatchers.Main){
                    view.text=sb.toString()
                }
            }
            else{
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,"Sorry record does not exist in the Database",Toast.LENGTH_LONG).show()
                }
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

     private fun getoldcar():cars{
         val carname=modelname.text.toString()
         val carcolor=colorname.text.toString()
         return cars(carname,carcolor)
     }

    private fun getnewcar():Map<String,Any>{
        val newcarname=newmodelname.text.toString()
        val newcarcolor=newcolorname.text.toString()
        val map= mutableMapOf<String,Any>()
        if(newcarname.isNotEmpty()){
            map["modelname"]=newcarname
        }
            if(newcarcolor.isNotEmpty()){
                map["color"]=newcarcolor
        }

        return map
    }

    private fun updatecar(oldcar: cars,newcar:Map<String,Any>)= CoroutineScope(Dispatchers.IO).launch {
        val carquery=carcollection.whereEqualTo("modelname",oldcar.modelname)
            .whereEqualTo("color",oldcar.color).get().await()

        if(carquery.documents.isNotEmpty()){
            try{
                for(documents in carquery){
                    carcollection.document(documents.id).set(
                        newcar,
                        SetOptions.merge()

                    )

                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,"Car not found in the database",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun DeleteCar(cars: cars)=CoroutineScope(Dispatchers.IO).launch {
        val carquery=carcollection.whereEqualTo("modelname",cars.modelname).whereEqualTo("color",cars.color).get().await()

        if(carquery.documents.isNotEmpty()){
            try{
                for(documents in carquery){
                    carcollection.document(documents.id).delete().await()
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity,"Record is been deleted",Toast.LENGTH_LONG).show()
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,"Sorry car not found",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun Transaction(carid:String)= CoroutineScope(Dispatchers.IO).launch {
        try{
            Firebase.firestore.runTransaction { transaction->
                val carreference=carcollection.document(carid)
                val cardocument=transaction.get(carreference)
                var currentcarname=cardocument["modelname"] as String
                currentcarname="Audi44"
                transaction.update(carreference,"modelname",currentcarname)
                null
            }.await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,"Transaction Successfull",Toast.LENGTH_LONG).show()
            }

        }catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_LONG).show()
            }
        }
    }

}