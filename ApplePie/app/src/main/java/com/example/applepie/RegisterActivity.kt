package com.example.applepie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameInput = findViewById(R.id.usernameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        passwordConfirmInput = findViewById(R.id.passwordConfirmInput)
        signupButton = findViewById(R.id.signupButton)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("users")
        auth = Firebase.auth

        signupButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val passwordConfirm = passwordConfirmInput.text.toString()

            if (username.isBlank() || email.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
                Toasty.warning(this, "Username, Email and Password can't be blank", Toast.LENGTH_SHORT, true).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toasty.warning(this, "Passwords must be at least 6 characters", Toast.LENGTH_SHORT, true).show()
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toasty.error(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT, true).show()
                return@setOnClickListener
            }

            databaseReference.orderByChild("info/username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toasty.error(this@RegisterActivity, "Username already exists", Toast.LENGTH_SHORT, true).show()
                    }
                    else {
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@RegisterActivity) {
                            if (it.isSuccessful) {
                                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val userCount = dataSnapshot.childrenCount.toInt()
//                                        Log.i("nqlog", userCount.toString())
                                        val newUserRef = databaseReference.child(userCount.toString())
                                        val user = User(email = email, password = password, username = username, id_user = userCount + 1)
                                        newUserRef.child("info").setValue(user).addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Toasty.success(this@RegisterActivity, "Sign up successfully", Toast.LENGTH_SHORT, true).show()
                                                finish()
                                            } else {
                                                Toasty.error(this@RegisterActivity, "Failed to add user to database", Toast.LENGTH_SHORT, true).show()
                                            }
                                        }
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Toasty.error(this@RegisterActivity, databaseError.message, Toast.LENGTH_SHORT, true).show()
                                    }
                                })
                            } else {
                                Toasty.error(this@RegisterActivity, "Invalid email or email is already in use", Toast.LENGTH_SHORT, true).show()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toasty.error(
                        this@RegisterActivity,
                        error.message,
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            })
        }

        loginText = findViewById(R.id.loginText)
        loginText.setOnClickListener {
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
        }
    }

    private lateinit var usernameInput: TextView
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var passwordConfirmInput: TextInputEditText
    private lateinit var signupButton: MaterialButton
    private lateinit var loginText: MaterialTextView

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
}