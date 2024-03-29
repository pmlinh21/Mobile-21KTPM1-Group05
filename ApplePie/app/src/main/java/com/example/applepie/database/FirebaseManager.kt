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

    /*

    setter : get data from firebase and set it to FirebaseDatabase
    getter: get data from FirebaseDatabase

    */

    interface DataCallback<T> {
        fun onDataReceived(data: T)
        fun onError(error: DatabaseError)
    }

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

    fun setUserInfo(callback: DataCallback<User>) {
        userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userInfo = dataSnapshot.getValue(User::class.java)!!
                    callback.onDataReceived(userInfo)
//                    Log.i("data",userInfo.toString())
                } else {
                    Log.d("UserInfo", "User info not found for UID")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle potential errors
                callback.onError(databaseError)
                Log.e("Firebase", "Error retrieving user info: ${databaseError.message}")
            }
        })
    }
    fun getUserInfo(): User {
        return userInfo
    }

    fun setUserList(callback: DataCallback<List<Lists>>) {
        userListsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempList = ArrayList<Lists>()
                for (snapshot in dataSnapshot.children) {
                    val lists = snapshot.getValue(Lists::class.java)
                    lists?.let {
                        tempList.add(it)
                    }
                }
                userList = tempList
                callback.onDataReceived(userList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.onError(databaseError)
                Log.e("Firebase", "Error retrieving user info: ${databaseError.message}")
            }
        })
    }
    fun getUserList(): List<Lists> {
        return userList
    }

    fun setUserTask(callback: DataCallback<List<Task>>) {
        userTasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempTask = ArrayList<Task>()
                for (snapshot in dataSnapshot.children) {
                    val task = snapshot.getValue(Task::class.java)
                    task?.let {
                        tempTask.add(it)
                    }
                }
                userTask= tempTask
                callback.onDataReceived(userTask)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback.onError(databaseError)
                Log.e("Firebase", "Error retrieving user info: ${databaseError.message}")
            }
        })
    }
    fun getUserTask(): List<Task> {
        return userTask
    }

}