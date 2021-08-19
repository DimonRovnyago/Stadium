package ru.study.stadium

import android.R
import android.app.ListActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import java.util.*

class MainActivity : ListActivity(), OnItemLongClickListener {
    val games_list = arrayOf(
        "Корабли", "Военная газета", "Скоро",
        "Скоро"
    )
    private var mAdapter: ArrayAdapter<String>? = null
    private val catNamesList = ArrayList(listOf(*games_list))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main);
        mAdapter = ArrayAdapter(
            this,
            R.layout.simple_list_item_1, catNamesList
        )
        listAdapter = mAdapter
        listView.onItemLongClickListener = this
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        Toast.makeText(
            applicationContext,
            "Вы выбрали " + l.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT
        ).show()
    }

    override fun onItemLongClick(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ): Boolean {
        val selectedItem = parent.getItemAtPosition(position).toString()
        mAdapter!!.remove(selectedItem)
        mAdapter!!.notifyDataSetChanged()
        Toast.makeText(
            applicationContext,
            "$selectedItem удалён.",
            Toast.LENGTH_SHORT
        ).show()
        return true
    }
}