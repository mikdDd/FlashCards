package com.example.flashcards

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {


    private var packageRecyclerView: RecyclerView? = null

    companion object {
        var packageArrayList: ArrayList<Package> = ArrayList()
        lateinit var dao : DatabaseDao
    }
    private lateinit var database : Database
    private var addPackageButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val context = this

        database = Room.databaseBuilder(this, Database::class.java, "database").build()
        dao = database.Dao()
        addPackageButton = findViewById(R.id.add_package_button)
        packageRecyclerView = findViewById(R.id.recyclerView)

        val job = GlobalScope.launch(Dispatchers.IO)
        {
            packageArrayList = ArrayList(dao.selectAllPackages())
            for(p in packageArrayList)
            {
                p.flashCards = ArrayList(dao.selectAllCards(p.id))
            }
        }
        runBlocking {
            job.join()
        }

        val editButtonResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data
                val position = data?.getIntExtra("position",0)
                val name = data?.getStringExtra("new_name")
                if(data?.getBooleanExtra("deleted",false)!!){
                    if (position != null) {
                        packageRecyclerView?.adapter?.notifyItemRemoved(position)
                    }
                } else {
                    position?.let {
                        if (name != null) {
                            packageArrayList[it].name = name
                            packageRecyclerView?.adapter?.notifyItemChanged(position)

                        }
                    }
                }
                if (position != null) {
                    packageRecyclerView?.adapter?.notifyItemChanged(position)
                }
            }
        }
        packageRecyclerView?.layoutManager = LinearLayoutManager(this)
        val packageAdapter = PackageRecyclerAdapter(packageArrayList,
            object: PackageRecyclerAdapter.EditButtonListener{
            override fun onEditButtonClick(position: Int) {
                val intent = Intent(context,FlashCardsListActivity::class.java)

                intent.putExtra("position",position)
                intent.putExtra("packageId", packageArrayList[position].id)
                editButtonResultLauncher.launch(intent)
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
                    val intent = Intent(context, TestModeActivity::class.java)
                    intent.putExtra("position", position)
                    startActivity(intent)
                }
            }
        )
        packageRecyclerView?.adapter =  packageAdapter

    }

    fun addPackageButtonClicked(view: View) {
        val intent = Intent(this, AddPackageActivity::class.java)
        addPackageResultLauncher.launch(intent)
    }

    private var addPackageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data
            val nameString = data?.getStringExtra("package_name").toString()

            Package(nameString)
                .let{
                    GlobalScope.launch(Dispatchers.IO)
                    {
                        it.id = dao.insertPackage(it)
                    }
                    packageArrayList.add(it)
                    packageRecyclerView?.adapter?.notifyItemInserted(packageArrayList.size)

                }
        }
    }

    fun onDownloadButtonClick(view: View) {
        val intent = Intent(this, DownloadPackageActivity::class.java)

        downloadResultLauncher.launch(intent)


    }
    private var downloadResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            packageRecyclerView?.adapter?.notifyItemInserted(packageArrayList.size)
        }
    }
}
