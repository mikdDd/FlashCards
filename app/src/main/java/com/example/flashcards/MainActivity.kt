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
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    private var packageRecyclerView: RecyclerView? = null
    companion object {
        var packageArrayList: ArrayList<Package> = ArrayList()
        lateinit var dao : DatabaseDao
    }
    lateinit var database : Database


    private var addPackageButton: FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var context = this

       // packageArrayList.add(Package("PAKIET!", arrayListOf(FlashCard("SLOWO!","TLUMACZENIE"), FlashCard("SLOWO2","TLUAMCZENIE"))))
        database = Room.databaseBuilder(this, Database::class.java, "database").build()
        dao = database.Dao()



        println("$packageArrayList.toString()")

        addPackageButton = findViewById(R.id.add_package_button)
        packageRecyclerView = findViewById(R.id.recyclerView)
        val job = GlobalScope.launch(Dispatchers.IO)
        {
            packageArrayList = ArrayList(dao.selectAllPackages())
            for(i in packageArrayList)
            {
                i.flashCards = ArrayList(dao.selectAllCards(i.id))
            }
        }
        runBlocking {
            job.join()
        }
        packageRecyclerView?.layoutManager = LinearLayoutManager(this)
        var packageAdapter = PackageRecyclerAdapter(packageArrayList,
            object: PackageRecyclerAdapter.EditButtonListener{
            override fun onEditButtonClick(position: Int) {
                val intent = Intent(context,FlashCardsListActivity::class.java)
                Log.i("MMM",position.toString())
                //var extra = Bundle()
               // extra.putSerializable("flash_card_list",packageArrayList[position].flashCards)
                intent.putExtra("position",position)
                intent.putExtra("packageId", packageArrayList[position].id)
                Log.i("MMM", packageArrayList[position].flashCards.toString())
                //intent.putExtra("",extra)
                startActivity(intent)
            }
        },
            object: PackageRecyclerAdapter.StudyButtonListener{
                override fun onStudyButtonClick(position: Int) {
                    val intent = Intent(context, StudyingActivity::class.java)
                    intent.putExtra("position",position)
                    startActivity(intent)
                }
            },
            object: PackageRecyclerAdapter.TestButtonListener{
                override fun onTestButtonClick(position: Int) {
                    val intent = Intent(context, TestActivity::class.java)
                    intent.putExtra("position", position)
                    startActivity(intent)
                }
            }
        )
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

            Package(nameString)
                .let{
                    GlobalScope.launch(Dispatchers.IO)
                    {
                        it.id = dao.insertPackage(it)
                        Log.d("test321", it.id.toString())

                    }
                    packageArrayList.add(it)
                    packageRecyclerView?.adapter?.notifyItemInserted(packageArrayList.size)

                }

        }
    }
}


//TODO podpiąć checkboxa learned
//TODO: Nauka - wyświetlanie słówek z pakietu, możliwość zaznaczenia słówka jako nauczonego

//TODO: Edycja pakietu - wyświetlanie listy słówek (recyclerview pewnie) -> (dodawanie i usuwanie słówek)
//TODO: Zapisywanie pakietów w bazie danych - odczytywanie ich z niej
//TODO: eksport i import pakietów do/z pliku txt żeby można było wysyłać gotowe pakiety fiszek innym