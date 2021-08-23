package ru.study.stadium


import android.os.Bundle
import android.app.ListActivity
import android.content.ComponentName
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import java.util.*
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toolbar
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.lang.Exception


class MainActivity : ListActivity() {
    //объявление переменных, отвечающих за объекты интерфейса
    lateinit var gamesListView: ListView

    //переменная авторизации Firebase и базы данных Firestore Cloud
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreCloudDB: FirebaseFirestore

    //список игр
    val gamesList = arrayOf("Корабли", "Военная газета", "Скоро", "Скоро")

    //служебные перемннные для отображения списка игр
    private var mAdapter: ArrayAdapter<String>? = null
    private val gamesNamesList = ArrayList(listOf(*gamesList))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        //авторизация в Firebase и в Firestore
        auth = Firebase.auth
        firestoreCloudDB = Firebase.firestore

        //инициализация всех объектов интерфейса
        gamesListView = findViewById<ListView>(android.R.id.list) as ListView

        //установка параметров объектов интерфейса
        //настройка Toolbar
        var actBar = getActionBar()
        actBar?.title = "Игры"

        //настройка adapter
        mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, gamesNamesList)
        gamesListView.adapter = mAdapter;

        Log.d("my", auth.currentUser!!.email.toString())
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, ID: Long) {
        super.onListItemClick(l, v, position, ID)
        when(position) {
            0 -> {
                //получение параметров пользователя с базы данных
                firestoreCloudDB.collection("shipwars/${auth.currentUser!!.email.toString()}/ships")
                    .document("Aurora")
                    .get()
                    .addOnSuccessListener { userData ->
                        var launchGameIntent = Intent()
                        var bull_speed = 500

                        try {
                            val reply = JSONObject(userData.data)
                            bull_speed = reply.getInt("bull_speed")
                        } catch (ex: Exception) {
                            firestoreCloudDB.collection("shipwars/${auth.currentUser!!.email.toString()}/ships")
                                .document("Aurora")
                                .set(hashMapOf(
                                    "bull_speed" to 500
                                ))
                        }

                        launchGameIntent.component = ComponentName(
                            "ru.study.stadium.games.shipwars",
                            "com.unity3d.player.UnityPlayerActivity"
                        );
                        launchGameIntent.putExtra("bull_speed", bull_speed.toString())
                        startActivity(launchGameIntent);
                    }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, ProfileActivity::class.java)
        startActivity(myIntent)

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //if(item.itemId == R.id.action_profile) startActivity(this, ProfileActivity::class.java)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

}