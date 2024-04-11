package com.example.applepie.database
//import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import com.example.applepie.model.DateTime
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task
import com.example.applepie.model.User
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
    private lateinit var userPomodoroRef: DatabaseReference
    private lateinit var userStopwatchRef: DatabaseReference

    private lateinit var userInfo: User
    private lateinit var userList: List<TaskList>
    private lateinit var userTask: List<Task>
    private lateinit var userPomodoro: List<DateTime>
    private lateinit var userStopwatch: List<DateTime>
    private lateinit var userStreak: List<DateTime>
    private lateinit var userAllowedNotiApp: List<String>

    /*

    setter :
        get ref/data from firebase and set it to FirebaseDatabase
        if u want to use setter methods, init in Main Activity and follow the sample code in getInfoFromFirebase()
    getter:
        get data from FirebaseDatabase

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

    fun setUserInfoRef(index: Int) {
        userInfoRef =  FirebaseDatabase.getInstance().getReference("users/$index/info")
    }

    fun setUserListsRef(index: Int) {
        userListsRef = FirebaseDatabase.getInstance().getReference("users/$index/lists")
    }

    fun setUserTasksRef(index: Int) {
        userTasksRef = FirebaseDatabase.getInstance().getReference("users/$index/tasks")
    }

    fun setUserPomodoroRef(index: Int) {
        userPomodoroRef = FirebaseDatabase.getInstance().getReference("users/$index/pomodoro")
    }

    fun setUserStopwatchRef(index: Int) {
        userStopwatchRef = FirebaseDatabase.getInstance().getReference("users/$index/stopwatch")
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

    fun setUserList(callback: DataCallback<List<TaskList>>) {
        userListsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempList = ArrayList<TaskList>()
                for (snapshot in dataSnapshot.children) {
                    val lists = snapshot.getValue(TaskList::class.java)
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
    fun getUserList(): List<TaskList> {
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

    fun setUserAllowedNotiApp(index: Int) {
        val userAllowedNotiAppRef = FirebaseDatabase.getInstance().getReference("users/$index/allowed_noti_app")
        val allowedPackageNames = mutableListOf<String>()

        userAllowedNotiAppRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the list to avoid duplicates
                allowedPackageNames.clear()

                // Retrieve the list of allowed package names
                for (packageNameSnapshot in dataSnapshot.children) {
                    val packageName = packageNameSnapshot.getValue(String::class.java)
                    if (packageName != null) {
                        allowedPackageNames.add(packageName)
                    }
                }

                Log.i("notiapp1",allowedPackageNames.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error case
                // ...
            }
        })

        userAllowedNotiApp = allowedPackageNames
    }

    fun getUserAllowedNotiApp(): List<String>{
        return userAllowedNotiApp
    }
    fun updateUserAllowedNotiApp(index: Int, allowedPackageNames: List<String>) {
        val userAllowedNotiAppRef =  FirebaseDatabase.getInstance().getReference("users/$index/allowed_noti_app")

        userAllowedNotiAppRef.setValue(allowedPackageNames)
    }

}