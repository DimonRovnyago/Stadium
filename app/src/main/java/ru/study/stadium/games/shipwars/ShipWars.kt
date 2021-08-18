package ru.study.stadium.games.shipwars

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import ru.study.stadium.R

class ShipWars : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    var TAG = "ShipWarsActivity"
    var bull_speed: Int = 0;
    var distanceText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ship_wars)

        distanceText = findViewById(R.id.distanceText) as TextView

        auth = Firebase.auth
        db.collection("users")
            .document(auth.currentUser!!.email.toString())
            .get()
            .addOnSuccessListener { result ->
                var reply = JSONObject(result.data)
                bull_speed = reply.getInt("ship_bull_speed")
                startNewRound()
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
    fun startNewRound() {
        distanceText!!.setText("18000")
    }
}