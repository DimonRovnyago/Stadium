package ru.study.stadium.login

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.INFO
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.study.stadium.MainActivity
import ru.study.stadium.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    var _emailText: EditText? = null
    var _passwordText: EditText? = null
    var _loginButton: Button? = null
    var _signupLink: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        _loginButton = findViewById(R.id.btn_login) as Button
        _signupLink = findViewById(R.id.link_signup) as TextView
        _passwordText = findViewById(R.id.input_password) as EditText
        _emailText = findViewById(R.id.input_email) as EditText
        _loginButton!!.setOnClickListener { login() }

        _signupLink!!.setOnClickListener {
            // Start the Signup activity
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGNUP)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    fun login() {
        Log.d(TAG, "Login")

        if (!validate()) {
            onLoginFailed()
            return
        }

        //прогресс логина
        val progressDialog = ProgressDialog(this@LoginActivity, R.style.AppTheme_Dark_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Login...")
        progressDialog.show()


        // TODO: Implement your own authentication logic here.


        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

        //проверка по базе
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
            Log.d(TAG, task.isSuccessful.toString())
            Log.d(TAG, "_______________________________________________")
            progressDialog.dismiss()
            if(task.isSuccessful) {
                _loginButton!!.isEnabled = false
                // On complete call either onLoginSuccess or onLoginFailed
                onLoginSuccess()
            }
            else {
                onLoginFailed()
            }
        })




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == Activity.RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish()
            }
        }
    }

    override fun onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true)
    }

    fun onLoginSuccess() {
        _loginButton!!.isEnabled = true
        finish()
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed\nCheck username and password", Toast.LENGTH_LONG).show()

        _loginButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true
        var tried = false

        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

        //проверка на почту
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText!!.error = "enter a valid email address"
            valid = false
        } else {
            _emailText!!.error = null
        }

        //проверка пароля на длину
        if (password.isEmpty() || password.length < 6) {
            _passwordText!!.error = "6 or more characters"
            valid = false
        } else {
            _passwordText!!.error = null
        }

        return valid
    }


    companion object {
        val TAG = "LoginActivity"
        private val REQUEST_SIGNUP = 0
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        Log.d(TAG, "User is ${currentUser.toString()}")

        if(currentUser != null) startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }



}