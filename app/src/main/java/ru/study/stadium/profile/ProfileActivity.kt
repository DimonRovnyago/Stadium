package ru.study.stadium

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import ru.study.stadium.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    //объявление переменных, отвечающих за объекты интерфейса
    var IProfilePhotoImage: ImageView? = null
    lateinit var INameText: TextView
    lateinit var ISignOutButton: Button

    //переменные данных пользователя
    private var name = ""
    private var email = ""

    //переменная авторизации Firebase и базы данных Firestore Cloud
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreCloudDB: FirebaseFirestore

    //тэг в логах
    var logTag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //авторизация в Firebase и в Firestore
        auth = Firebase.auth
        firestoreCloudDB = Firebase.firestore

        //инициализация всех объектов интерфейса
        IProfilePhotoImage = findViewById(R.id.profilePhotoImage) as ImageView
        INameText = findViewById(R.id.nameText) as TextView;
        ISignOutButton = findViewById(R.id.signOutButton) as Button

        //установка параметров объектов интерфейса
        IProfilePhotoImage!!.setImageResource(R.mipmap.no_avatar)
        INameText!!.setTextColor(Color.YELLOW)

        //действие на нажатие на кнопку "Выйти"
        ISignOutButton.setOnClickListener {
            //выход из учетной записи на уровни Firebase
            auth.signOut()
            //запуск LoginActivity и завершение ProfileActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            //анимация запуска LoginActivity
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

        //получение параметров пользователя с базы данных
        firestoreCloudDB.collection("users")
            .document(auth.currentUser!!.email.toString())
            .get()
            .addOnSuccessListener { userData ->
                val reply = JSONObject(userData.data)

                name = reply.getString("name")

                //установка данных пользователя в поля
                INameText.setText(name)
            }
            .addOnFailureListener {exception ->
                Log.w(logTag, "Error getting documents.", exception)
            }
    }
}