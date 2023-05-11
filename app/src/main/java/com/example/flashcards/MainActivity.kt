package com.example.flashcards

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var packageRecyclerView: RecyclerView? = null
    companion object {  var packageArrayList: ArrayList<Package> = ArrayList()}

    private var addPackageButton: FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       // packageArrayList.add(Package("PAKIET!", arrayListOf(FlashCard("SLOWO!","TLUMACZENIE"), FlashCard("SLOWO2","TLUAMCZENIE"))))

        println("$packageArrayList.toString()")

        addPackageButton = findViewById(R.id.add_package_button)
        packageRecyclerView = findViewById(R.id.recyclerView)

        packageRecyclerView?.layoutManager = LinearLayoutManager(this)
        var context = this
        var packageAdapter = PackageRecyclerAdapter(packageArrayList, object: PackageRecyclerAdapter.EditButtonListener{
            override fun onEditButtonClick(position: Int) {
                val intent = Intent(context,FlashCardsListActivity::class.java)
                Log.i("MMM",position.toString())
                //var extra = Bundle()
               // extra.putSerializable("flash_card_list",packageArrayList[position].flashCards)
                intent.putExtra("position",position)
                Log.i("MMM", packageArrayList[position].flashCards.toString())
                //intent.putExtra("",extra)
                startActivity(intent)
            }
        })

        packageRecyclerView?.adapter =  packageAdapter


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
            nameString.let { Package(it) }
                .let { packageArrayList.add(it) }

            packageRecyclerView?.adapter?.notifyItemInserted(packageArrayList.size)
        }


    }


}

//TODO podpiąć checkboxa learned
//TODO: Nauka - wyświetlanie słówek z pakietu, możliwość zaznaczenia słówka jako nauczonego
//TODO: Zapisywanie pakietów w bazie danych - odczytywanie ich z niej
//TODO: eksport i import pakietów do/z pliku txt żeby można było wysyłać gotowe pakiety fiszek innym