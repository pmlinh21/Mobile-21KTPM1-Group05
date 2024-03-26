package com.example.applepie.database
//import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


object FirebaseManager {
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance("https://applepie-231df-default-rtdb.asia-southeast1.firebasedatabase.app/") }

    private lateinit var userRef: DatabaseReference
    private lateinit var userInfoRef: DatabaseReference
    private lateinit var userListsRef: DatabaseReference
    private lateinit var userTasksRef: DatabaseReference

    private lateinit var userInfo: User
    private lateinit var userList: List<Lists>
    private lateinit var userTask: List<Task>

    fun getFirebaseDatabase(): FirebaseDatabase {
        return database
    }

    fun setUserRef(index: Int) {
        userRef =  FirebaseDatabase.getInstance().getReference("users/$index")
    }
    fun getUserRef() : DatabaseReference{
        return userRef
    }

    fun setUserInfoRef(index: Int) {
        userInfoRef =  FirebaseDatabase.getInstance().getReference("users/$index/info")
    }
    fun getUserInfoRef(): DatabaseReference {
        return userInfoRef
    }

    fun setUserListsRef(index: Int) {
        userListsRef = FirebaseDatabase.getInstance().getReference("users/$index/lists")
    }
    fun getUserListsRef(): DatabaseReference {
        return userListsRef
    }

    fun setUserTasksRef(index: Int) {
        userTasksRef = FirebaseDatabase.getInstance().getReference("users/$index/tasks")
    }
    fun getUserTaskRef(): DatabaseReference {
        return userTasksRef
    }

    fun setUserInfo() {
        userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userInfo = dataSnapshot.getValue(User::class.java)!!
                    Log.i("data",userInfo.toString())
                } else {
                    Log.d("UserInfo", "User info not found for UID")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle potential errors
                Log.e("Firebase", "Error retrieving user info: ${databaseError.message}")
            }
        })
    }
    fun getUserInfo(): User {
        return userInfo
    }

}