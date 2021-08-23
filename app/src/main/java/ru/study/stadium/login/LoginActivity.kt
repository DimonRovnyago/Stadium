package ru.study.stadium.login

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.study.stadium.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.study.stadium.MainActivity


class LoginActivity : AppCompatActivity() {
    //переменные, отвечающих за объекты интерфейса
    private lateinit var IEmailText: EditText
    private lateinit var IPasswordText: EditText
    lateinit var ILoginButton: Button
    lateinit var ISignupLink: TextView

    //переменные почты и пароля
    private var email = "";
    private var password = "";

    //переменная авторизации Firebase
    private lateinit var authInFirebase: FirebaseAuth

    //тэг в логах
    val logTag = "LoginActivity"

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES; }
        setContentView(R.layout.activity_login)

        //авторизация в Firebase
        authInFirebase = Firebase.auth

        //инициализация всех объектов
        ILoginButton = findViewById(R.id.btn_login) as Button
        ISignupLink = findViewById(R.id.link_signup) as TextView
        IPasswordText = findViewById(R.id.input_password) as EditText
        IEmailText = findViewById(R.id.input_email) as EditText

        //действие на нажатие на кнопку "Вход"
        ILoginButton.setOnClickListener { Login() }

        //действие на нажатие на ссылку регистрации
        ISignupLink.setOnClickListener {
            //запуск SignupActivity и завершение LoginActivity
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
            //анимация запуска SignupActivity
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    //функция логина в приложения
    private fun Login() {
        Log.d(logTag, "Login started")

        //получаем логин и пароль из полей ввода
        email = IEmailText.text.toString()
        password = IPasswordText.text.toString()

        //завершаем логин с ошибкой, если почта или пароль некорректные
        if (!ValidateEmailAndPassword()) {
            ShowLoginFailed("\nIncorrect email or password")
            return
        }

        //инициализация прогресс-бара логина
        val ILoginProgressDialog = ProgressDialog(this, R.style.AppTheme_Dark_Dialog)
        ILoginProgressDialog.isIndeterminate = true
        ILoginProgressDialog.setMessage("Login...")
        ILoginProgressDialog.show()

        //попытка входа по указанной почте и паролю
        authInFirebase.signInWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener<AuthResult?> { loginResult ->

            Log.d(logTag, "Is login successful: ${loginResult.isSuccessful.toString()}")

            //убираем прогресс-бар логина
            ILoginProgressDialog.dismiss()

            //если логин прошёл успешно
            if(loginResult.isSuccessful) ShowLoginSuccess()
            //если логин провалился
            else ShowLoginFailed("\nCheck email and password")
        })
    }

    //действие при нажатии кнопки назад
    override fun onBackPressed() {
        //отключаем возможность запустить MainActivity кнопкой назад
        moveTaskToBack(true)
    }

    //вызывается, если логин прошёл успешно
    private fun ShowLoginSuccess() {
        //запуск MainActivity и завершение LoginActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        //анимация запуска MainActivity
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
    }

    //вызывается, если логин прошёл с ошибкой
    private fun ShowLoginFailed(occuredError: String) {
        Toast.makeText(baseContext, "Login failed${occuredError}", Toast.LENGTH_LONG).show()
    }

    //вызывается для проверки валидности введённого логина и пароля
    private fun ValidateEmailAndPassword(): Boolean {
        var isEmailAndPasswordValid = true

        //проверка почты
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            IEmailText.error = "enter a valid email address"
            isEmailAndPasswordValid = false
        } else {
            IEmailText.error = null
        }

        //проверка пароля на длину
        if (password.isEmpty() || password.length < 6) {
            IPasswordText.error = "6 or more characters"
            isEmailAndPasswordValid = false
        } else {
            IPasswordText.error = null
        }

        return isEmailAndPasswordValid
    }

    override fun onStart() {
        super.onStart()

        //получаем текущего пользователя
        val currentUser = authInFirebase.currentUser

        //если пользователь уже залогинен, то пропускаем экран логина
        if(currentUser != null) {
            Log.d(logTag, "User already logged in as ${currentUser.toString()}")

            //запускаем MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            //анимация запуска MainActivity
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }

    }



}