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
import ru.study.stadium.MainActivity
import ru.study.stadium.R
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.auth.AuthResult

import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    var _nameText: EditText? = null
    var _emailText: EditText? = null
    var _passwordText: EditText? = null
    var _reEnterPasswordText: EditText? = null
    var _signupButton: Button? = null
    var _loginLink: TextView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth

        _nameText = findViewById(R.id.input_name) as EditText
        _emailText = findViewById(R.id.input_email) as EditText
        _passwordText = findViewById(R.id.input_password) as EditText
        _reEnterPasswordText = findViewById(R.id.input_reEnterPassword) as EditText

        _signupButton = findViewById(R.id.btn_signup) as Button
        _loginLink = findViewById(R.id.link_login) as TextView

        _signupButton!!.setOnClickListener { signup() }

        _loginLink!!.setOnClickListener {
            // Finish the registration screen and return to the Login activity
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    fun signup() {
        Log.d(TAG, "Signup")

        if (!validate()) {
            onSignupFailed("")
            return
        }

        //прогресс регистрации
        val progressDialog = ProgressDialog(this@SignupActivity,
            R.style.AppTheme_Dark_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        val name = _nameText!!.text.toString()
        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()
        val reEnterPassword = _reEnterPasswordText!!.text.toString()

        //проверка по базе
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
            Log.d(LoginActivity.TAG, task.isSuccessful.toString())
            Log.d(LoginActivity.TAG, "_______________________________________________")
            progressDialog.dismiss()
            if(task.isSuccessful) {

                _signupButton!!.isEnabled = false

                //добавляем имя и email в базу
                val user = hashMapOf(
                    "email" to email,
                    "name" to name,
                    "photo" to "no",
                    "ship_bull_speed" to 500
                )
                db.collection("users")
                    .document(email)
                    .set(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${email}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }

                onSignupSuccess()
            }
            else {
                onSignupFailed("User with this email already exists")
            }
        })
    }


    fun onSignupSuccess() {
        _signupButton!!.isEnabled = true
//        setResult(Activity.RESULT_OK, null)
//        finish()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun onSignupFailed(error: String) {
        Toast.makeText(baseContext, "SignUp failed\n${error}", Toast.LENGTH_LONG).show()

        _signupButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val name = _nameText!!.text.toString()
        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()
        val reEnterPassword = _reEnterPasswordText!!.text.toString()


        if (name.isEmpty()) {
            _nameText!!.error = "Fill name"
            valid = false
        } else {
            _nameText!!.error = null
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText!!.error = "enter a valid email address"
            valid = false
        } else {
            _emailText!!.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText!!.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            _passwordText!!.error = null
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length < 4 || reEnterPassword.length > 10 || reEnterPassword != password) {
            _reEnterPasswordText!!.error = "Password Do not match"
            valid = false
        } else {
            _reEnterPasswordText!!.error = null
        }

        return valid
    }

    companion object {
        val TAG = "SignupActivity"
    }
}