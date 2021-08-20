package ru.study.stadium.games.shipwars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.common.math.IntMath.pow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import ru.study.stadium.R
import java.lang.Math.asin

class ShipWars : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val firestoreCloudDB = Firebase.firestore

    var LogTag = "ShipWarsActivity"

    //скорости пуль игроков
    var my_bull_speed = 0;
    var enemy_bull_speed = 0;

    //скорости кораблей игроков
    var my_ship_speed = 0;
    var enemy_ship_speed = 0;

    //здоровья кораблей
    var my_ship_xp = 0;
    var enemy_ship_xp = 0;

    //дистанция
    var distance = 18000;
    var new_distance = distance;

    //угол выстрела
    var correct_angle: Double = 0.0;
    var players_angle: Double = 0.0;

    var distance_TextView: TextView? = null
    var player_angle_EditText: EditText? = null;
    var ok_Button: Button? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ship_wars)

        distance_TextView = findViewById(R.id.distance_TextView) as TextView
        player_angle_EditText = findViewById(R.id.player_angle_EditText) as EditText
        ok_Button = findViewById(R.id.ok_Button) as Button

        player_angle_EditText!!.isEnabled = false
        ok_Button!!.setOnClickListener {

        }

        auth = Firebase.auth
        firestoreCloudDB.collection("shipWars")
                //получаем скорость пули и корабля
            .document(auth.currentUser!!.email.toString())
            .get()
            .addOnSuccessListener { result ->

                //получаем с сервера данные корабля
                var reply = JSONObject(result.data)
                my_bull_speed = reply.getInt("bull_speed")
                my_ship_speed = reply.getInt("ship_speed")
                my_ship_xp = reply.getInt("ship_xp")

                startNewRound()
            }
            .addOnFailureListener {exception ->
                //ошибка получения данных (если не существуют)
                Log.w(LogTag, "Error getting ship params.", exception)

                //запись даннных на сервер
                setParamsIfNotExist()

                startNewRound()
            }
    }
    fun startNewRound() {
        distance_TextView!!.setText(distance) //устанавливаем текущей текст дистанции
        enemy_ship_xp = my_ship_xp //здоровье врага = нашему
        enemy_bull_speed = my_bull_speed //скорость пули врага = нашей
        enemy_ship_speed = my_ship_speed //скорость корабля врага = нашей

        while(new_distance > 0) {
            while(distance != new_distance) {
                distance--
                distance_TextView!!.setText(distance)
            }
            correct_angle = asin((distance/pow(my_bull_speed, 2))*9.81)/2

            player_angle_EditText!!.isEnabled = true


            new_distance -= (my_ship_speed + enemy_ship_speed)
        }
    }

    fun setParamsIfNotExist() {
        firestoreCloudDB.collection("shipWars")
            .document(auth.currentUser!!.email.toString())
            .set(hashMapOf(
                "bull_speed" to 500,
                "ship_speed" to 500,
                "ship_xp" to 1000
            ))
    }
}