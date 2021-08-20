package ru.study.stadium

import android.app.ActionBar
import android.os.Bundle
import android.app.ListActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import java.util.*
import android.content.Intent
import android.view.MenuItem


class MainActivity : ListActivity() {
    val GamesList = arrayOf(
        "Корабли", "Военная газета", "Скоро",
        "Скоро"
    )

    lateinit var gamesListView: ListView
    lateinit var toolbar: Toolbar

    private var mAdapter: ArrayAdapter<String>? = null
    private val GamesNamesList = ArrayList(listOf(*GamesList))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        gamesListView = findViewById<ListView>(android.R.id.list) as ListView
        toolbar = findViewById(R.id.toolbar) as Toolbar

        toolbar.setTitle("Игры")
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.back))
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        })


        mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, GamesNamesList)

        gamesListView.setAdapter(mAdapter);
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, ID: Long) {
        super.onListItemClick(l, v, position, ID)
        Toast.makeText(applicationContext,
            "Вы выбрали " + l.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT
        ).show()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_back -> {
            // User chose the "Settings" item, show the app settings UI...
            startActivity(Intent(this, ProfileActivity::class.java))
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }


}