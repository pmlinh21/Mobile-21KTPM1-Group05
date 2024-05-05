package com.example.applepie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)

        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        auth = FirebaseAuth.getInstance()
        preferenceManager = PreferenceManager(this)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toasty.warning(this, "Email and Password can't be blank", Toast.LENGTH_SHORT, true).show()
                return@setOnClickListener
            }

            if (username.contains('@')) {
                auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        Log.d("login","ok")
                        databaseReference.orderByChild("info/email").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (userSnapshot in snapshot.children) {
                                        val userIndex = userSnapshot.key!!.toInt()
                                        Log.i("loginactivity", userIndex.toString())
                                        preferenceManager.setIndex(userIndex)
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })

                        preferenceManager.setLogin(true)
                        preferenceManager.setUsername(username)

                        val mainActivity = Intent(this, MainActivity::class.java)
                        startActivity(mainActivity)

                        finish()
                    } else {
                        Toasty.error(
                            this,
                            "Username or password is incorrect",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }
            }
            else {
                databaseReference.orderByChild("info/username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val userIndex = userSnapshot.key!!.toInt()

                                val user = userSnapshot.child("info").getValue(User::class.java)

                                if (user != null) {
//                                    Log.i("nqlog", userSnapshot.toString())
                                    if (user.password == password) {
                                        preferenceManager.setLogin(true)
                                        preferenceManager.setUsername(username)
                                        preferenceManager.setIndex(userIndex)

                                        val mainActivity = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(mainActivity)

                                        finish()
                                    } else {
                                        Toasty.error(
                                            this@LoginActivity,
                                            "Username or password is incorrect",
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                    }
                                }
                            }
                        } else {
                            Toasty.error(
                                this@LoginActivity,
                                "Username or password is incorrect",
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toasty.error(
                            this@LoginActivity,
                            error.message,
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                })
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

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var preferenceManager: PreferenceManager
}