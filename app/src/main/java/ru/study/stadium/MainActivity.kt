package ru.study.stadium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    var profilePhotoImage: ImageView? = null
    var nameText: TextView? = null
    var signOutButton: Button? = null


    var TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profilePhotoImage = findViewById(R.id.profilePhotoImage) as ImageView
        nameText = findViewById(R.id.nameText) as TextView
        signOutButton = findViewById(R.id.signOutButton) as Button

        auth = Firebase.auth
        db.collection("users")
            .document(auth.currentUser!!.email.toString())
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "${result.id} => ${result.data}")
                var reply = JSONObject(result.data)

                var name = reply.getString("name")
                var photo = reply.getString("photo")

                Log.d("JSON", name)
                Log.d("JSON", photo)

                nameText!!.setText(name)
            }
            .addOnFailureListener {exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }



        signOutButton!!.setOnClickListener {
            auth.signOut()
        }


    }
}