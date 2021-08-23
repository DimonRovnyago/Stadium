package ru.study.stadium.games

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import ru.study.stadium.R
import android.content.Intent
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import ru.study.stadium.MainActivity


class GameResultActivity : AppCompatActivity() {
    private lateinit var IResultShotsS: TextView
    private lateinit var IResultShotsF: TextView
    private lateinit var IBackToMainMenuButton: Button


    //переменная авторизации Firebase и базы данных Firestore Cloud
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreCloudDB: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        //авторизация в Firebase и в Firestore
        auth = Firebase.auth
        firestoreCloudDB = Firebase.firestore

        IResultShotsS = findViewById(R.id.resultShotsS) as TextView
        IResultShotsF = findViewById(R.id.resultShotsF) as TextView
        IBackToMainMenuButton = findViewById(R.id.button) as Button

        val returnedRes = intent
        val shotsS = returnedRes.getIntExtra("shotsS", 111)
        val shotsF = returnedRes.getIntExtra("shotsF", 111)

        IResultShotsS.text = shotsS.toString()
        IResultShotsF.text = shotsF.toString()

        firestoreCloudDB.collection("shipwars")
            .document(auth.currentUser!!.email.toString())
            .get()
            .addOnSuccessListener { userData ->
                val reply = JSONObject(userData.data)
                var shotsS1 = reply.getInt("shotsS")
                var shotsF1 = reply.getInt("shotsF")

                firestoreCloudDB.collection("shipwars")
                    .document(auth.currentUser!!.email.toString())
                    .set(hashMapOf(
                        "shotsS" to shotsS1 + shotsS,
                        "shotsF" to shotsF1 + shotsF
                    ))
                    .addOnSuccessListener { result ->
                        IBackToMainMenuButton.setOnClickListener{
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                    }

            }





    }
}