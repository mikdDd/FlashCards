package com.example.flashcards

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var packageRecyclerView: RecyclerView? = null
    private var packageArrayList: ArrayList<Package> = ArrayList()
    private var addPackageButton: FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        println("$packageArrayList.toString()")

        addPackageButton = findViewById(R.id.add_package_button)
        packageRecyclerView = findViewById(R.id.recyclerView)

        packageRecyclerView?.layoutManager = LinearLayoutManager(this)

        packageRecyclerView?.adapter =  PackageRecyclerAdapter(packageArrayList)


    }

    fun addPackageButtonClicked(view: View) {


        val intent = Intent(this, AddPackageActivity::class.java)
        resultLauncher.launch(intent)

    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data
            val nameString = data?.getStringExtra("package_name").toString()
            println(nameString)
            nameString.let { Package(it, arrayListOf(Word("T","T"))) }
                .let { packageArrayList.add(it) }

            packageRecyclerView?.adapter?.notifyItemInserted(packageArrayList.size)
        }


    }
}

//TODO: Edycja pakietu - wyświetlanie listy słówek (recyclerview pewnie) -> (dodawanie i usuwanie słówek)
//TODO: Nauka - wyświetlanie słówek z pakietu, możliwość zaznaczenia słówka jako nauczonego
//TODO: Zapisywanie pakietów w bazie danych - odczytywanie ich z niej
//TODO: eksport i import pakietów do/z pliku txt żeby można było wysyłać gotowe pakiety fiszek innym