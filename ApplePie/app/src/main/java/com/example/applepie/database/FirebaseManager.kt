package com.example.applepie.database
//import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


object FirebaseManager {
//    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }

//        fun getFirebaseAuth(): FirebaseAuth {
//            return auth
//        }

    fun getFirebaseDatabase(): FirebaseDatabase {
        return database
    }

//    fun getUsersRef() : DatabaseReference{
//        return this.getFirebaseDatabase().getReference("users")
//    }
//
//    fun getUser(selectedIndex: Int){
//        val usersRef = getUsersRef()
//
//        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
//                    val users = dataSnapshot.children
//                    var index = 0
//
//                    for (userSnapshot in users) {
//                        if (index == selectedIndex) {
//                            val user = userSnapshot.getValue(
//                                User::class.java
//                            )
//
//                            Log.i("user",user.toString())
//                            break
//                        }
//                        index++
//                    }
//                } else {
//                    Log.d("User", "No users found in the database")
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle potential errors
//                Log.e("Firebase", "Error retrieving users: " + databaseError.message)
//            }
//        })
//    }

}