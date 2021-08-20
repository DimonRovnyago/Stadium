package ru.study.stadium.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.study.stadium.R

import com.google.firebase.auth.AuthResult

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.study.stadium.MainActivity


class SignupActivity : AppCompatActivity() {
    //объявление переменных, отвечающих за объекты
    private lateinit var INameText: EditText
    private lateinit var IEmailText: EditText
    private lateinit var IPasswordText: EditText
    private lateinit var IReEnterPasswordText: EditText
    lateinit var ISignupButton: Button
    lateinit var ILoginLink: TextView

    //переменные данных для регистрации
    private var name = ""
    private var email = ""
    private var password = ""
    private var reEnterPassword = ""

    //переменные авторизации Firebase и базы данных Firestore Cloud
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreCloudDB: FirebaseFirestore

    //тэг в логах
    val logTag = "LoginActivity"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //авторизация в Firebase и в Firestore
        auth = Firebase.auth
        firestoreCloudDB = Firebase.firestore

        //инициализация всех объектов интерфейса
        INameText = findViewById(R.id.input_name) as EditText
        IEmailText = findViewById(R.id.input_email) as EditText
        IPasswordText = findViewById(R.id.input_password) as EditText
        IReEnterPasswordText = findViewById(R.id.input_reEnterPassword) as EditText
        ISignupButton = findViewById(R.id.btn_signup) as Button
        ILoginLink = findViewById(R.id.link_login) as TextView

        //действие на нажатие на кнопку "Создать аккаунт"
        ISignupButton.setOnClickListener { Signup() }

        //действие на нажатие на ссылку логина
        ILoginLink.setOnClickListener {
            //запуск LoginActivity и завершение SignupActivity
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
            //анимация запуска LoginActivity
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    private fun Signup() {
        Log.d(logTag, "Signup started")

        //получаем имя, логин и пароль из полей ввода
        name = INameText.text.toString()
        email = IEmailText.text.toString()
        password = IPasswordText.text.toString()
        reEnterPassword = IReEnterPasswordText.text.toString()

        //проверка имени, почты и пароля на корректность
        if (!Validate()) {
            ShowSignupFailed("")
            return
        }

        //инициализация прогресс-бара регистрации
        val ISignupProgressDialog = ProgressDialog(this, R.style.AppTheme_Dark_Dialog)
        ISignupProgressDialog.isIndeterminate = true
        ISignupProgressDialog.setMessage("Creating Account...")
        ISignupProgressDialog.show()

        //проверка по базе
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener<AuthResult?> { signupResult ->
            Log.d(logTag, "Is signup successful: ${signupResult.isSuccessful.toString()}")

            //убираем прогресс-бар регистрации
            ISignupProgressDialog.dismiss()

            //если регистрация прошла успешно
            if(signupResult.isSuccessful) {
                //добавляем имя и email в базу
                firestoreCloudDB.collection("users")
                    .document(email)
                    .set(hashMapOf(
                        "email" to email,
                        "name" to name,
                        "photo" to "no",
                    ))

                ShowSignupSuccess()
            }
            //если регистрация провалилась
            else ShowSignupFailed("\nUser with this email already exists")
        })
    }

    //вызывается, если регистрация аккаунта прошла успешно
    private fun ShowSignupSuccess() {
        //запуск MainActivity и завершение SignupActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        //анимация запуска MainActivity
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
    }

    //вызывается, если регистрация аккаунта провалилась
    private fun ShowSignupFailed(occuredError: String) {
        Toast.makeText(baseContext, "SignUp failed${occuredError}", Toast.LENGTH_LONG).show()
    }

    //вызывается для проверки валидности имени, введённого логина и пароля
    private fun Validate(): Boolean {
        var isNameAndEmailAndPasswordValid = true

        //проверка имени
        if (name.isEmpty()) {
            INameText.error = "Fill name"
            isNameAndEmailAndPasswordValid = false
        } else {
            INameText.error = null
        }

        //проверка почты
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            IEmailText.error = "enter a valid email address"
            isNameAndEmailAndPasswordValid = false
        } else {
            IEmailText.error = null
        }

        //проверка пароля на длину
        if (password.isEmpty() || password.length < 6) {
            IPasswordText.error = "6 or more characters"
            isNameAndEmailAndPasswordValid = false
        } else {
            IPasswordText.error = null
        }

        //проверка повтора пароля
        if (reEnterPassword != password) {
            IReEnterPasswordText.error = "Password Do not match"
            isNameAndEmailAndPasswordValid = false
        } else {
            IReEnterPasswordText.error = null
        }

        return isNameAndEmailAndPasswordValid
    }
}