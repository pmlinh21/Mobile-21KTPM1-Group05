package com.example.applepie

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.applepie.database.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import es.dmoral.toasty.Toasty

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        auth = FirebaseAuth.getInstance()
        preferenceManager = PreferenceManager(this)

        loginButton.setOnClickListener {
            val email = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
                if (it.isSuccessful) {
//                    Toast.makeText(this, "Login successfully!", Toast.LENGTH_SHORT).show()

                    preferenceManager.setLogin(true)
                    preferenceManager.setUsername(email)

                    val mainActivity = Intent(this, MainActivity::class.java)
                    startActivity(mainActivity)

                    finish()
                } else {
//                    Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show()
                    Toasty.error(this, "Username or password is incorrect", Toast.LENGTH_SHORT, true).show()
                }
            }
        }

        signupText = findViewById(R.id.signupText)
        signupText.setOnClickListener {
            val registerActivity = Intent(this, RegisterActivity::class.java)
            startActivity(registerActivity)
//            finish()
        }

    }

    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signupText: MaterialTextView

    private lateinit var auth: FirebaseAuth
    private lateinit var preferenceManager: PreferenceManager
}