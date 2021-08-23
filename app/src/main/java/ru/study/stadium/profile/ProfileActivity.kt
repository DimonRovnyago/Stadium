package ru.study.stadium

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.utils.Easing
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import lecho.lib.hellocharts.view.PieChartView
import org.json.JSONObject
import ru.study.stadium.login.LoginActivity
import lecho.lib.hellocharts.model.PieChartData

import lecho.lib.hellocharts.model.SliceValue
import java.lang.Exception


class ProfileActivity : AppCompatActivity() {
    //объявление переменных, отвечающих за объекты интерфейса
    var IProfilePhotoImage: ImageView? = null
    lateinit var INameText: TextView
    lateinit var IEmailText: TextView
    lateinit var ISignOutButton: Button
    lateinit var pieChartView: PieChartView

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
        IEmailText = findViewById(R.id.emailText) as TextView;
        ISignOutButton = findViewById(R.id.signOutButton) as Button
        pieChartView = findViewById(R.id.chart) as PieChartView;

        //установка параметров объектов интерфейса
        IProfilePhotoImage!!.setImageResource(R.mipmap.no_avatar)

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

        //получение данных пользователя с БД
        getUserProfileData()

        //получение статистики пользователя с БД
        getUserStats()
    }

    //вызывается для получения статистики пользователя с БД
    private fun getUserStats() {
        firestoreCloudDB.collection("shipwars")
            .document(auth.currentUser!!.email.toString())
            .get()
            .addOnSuccessListener { userData ->
                var shotsS = 0
                var shotsF = 0
                try {
                    val reply = JSONObject(userData.data)
                    shotsS = reply.getInt("shotsS")
                    shotsF = reply.getInt("shotsF")
                } catch (ex: Exception) {
                    firestoreCloudDB.collection("shipwars")
                        .document(auth.currentUser!!.email.toString())
                        .set(hashMapOf(
                            "shotsS" to 0,
                            "shotsF" to 0
                        ))
                }


                var pieData: MutableList<SliceValue> = ArrayList()
                pieData.add(SliceValue(shotsS.toFloat(), resources.getColor(R.color.correct)).setLabel("Successful: $shotsS"))
                pieData.add(SliceValue(shotsF.toFloat(), resources.getColor(R.color.incorrect)).setLabel("Failed: $shotsF"))
                val pieChartData = PieChartData(pieData)

                pieChartData.setHasLabels(true)
                pieChartData.setHasLabelsOutside(false)
                pieChartData.setHasCenterCircle(true)
                pieChartView.pieChartData = pieChartData

            }
    }

    //вызывается для получения данных пользователя с БД
    private fun getUserProfileData() {
        firestoreCloudDB.collection("users")
            .document(auth.currentUser!!.email.toString())
            .get()
            .addOnSuccessListener { userData ->
                val reply = JSONObject(userData.data)

                name = reply.getString("name")
                email = reply.getString("email")

                //установка данных пользователя в поля
                INameText.setText(name)
                IEmailText.setText(email)
            }
    }

}