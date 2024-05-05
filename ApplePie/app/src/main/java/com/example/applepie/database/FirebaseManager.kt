package com.example.applepie.database
//import com.google.firebase.auth.FirebaseAuth

import android.util.Log
import android.widget.Toast
import com.example.applepie.database.FirebaseManager.userList
import com.example.applepie.model.DateTime
import com.example.applepie.model.Music
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task
import com.example.applepie.model.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Comment


object FirebaseManager {
    private val database: FirebaseDatabase by lazy { FirebaseDatabase.getInstance("https://applepie-231df-default-rtdb.asia-southeast1.firebasedatabase.app/") }

    private lateinit var userRef: DatabaseReference
    private lateinit var userInfoRef: DatabaseReference
    private lateinit var userListsRef: DatabaseReference
    private lateinit var userTasksRef: DatabaseReference

    private lateinit var userInfo: User
    private lateinit var userList: List<TaskList>
    private lateinit var userTask: List<Task>
    private lateinit var userPomodoro: List<DateTime>
    private lateinit var userStopwatch: List<DateTime>
    private lateinit var userStreak: List<DateTime>
    private lateinit var userBlockNotiApp: List<String>
    private lateinit var userMusic: Music
    private var userReminder: Int = 1

    private val listeners = mutableListOf<DataUpdateListener>()

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

    fun addDataUpdateListener(listener: DataUpdateListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removeDataUpdateListener(listener: DataUpdateListener) {
        listeners.remove(listener)
    }

    private fun notifyDataChanged() {
        listeners.forEach { it.updateData() }
    }

    fun setUserInfo(callback: DataCallback<User>) {
        userInfoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userInfo = dataSnapshot.getValue(User::class.java)!!
                    callback.onDataReceived(userInfo)
                    notifyDataChanged()
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
        userListsRef.addValueEventListener(object : ValueEventListener {
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
                notifyDataChanged()
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
        userTasksRef.addValueEventListener(object : ValueEventListener {
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
                notifyDataChanged()
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

    fun setUserBlockNotiApp(index: Int) {
        val userBlockNotiAppRef = FirebaseDatabase.getInstance().getReference("users/$index/block_noti_app")
        val blockPackageNames = mutableListOf<String>()

        userBlockNotiAppRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the list to avoid duplicates
                blockPackageNames.clear()

                // Retrieve the list of allowed package names
                for (packageNameSnapshot in dataSnapshot.children) {
                    val packageName = packageNameSnapshot.getValue(String::class.java)
                    if (packageName != null) {
                        blockPackageNames.add(packageName)
                    }
                }

                Log.i("data",blockPackageNames.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error case
                // ...
            }
        })

        userBlockNotiApp = blockPackageNames
    }

    fun getUserBlockNotiApp(): List<String>{
        return userBlockNotiApp
    }
    fun updateUserBlockNotiApp(index: Int, blockPackageNames: List<String>) {
        val userBlockNotiApp =  FirebaseDatabase.getInstance().getReference("users/$index/block_noti_app")

        userBlockNotiApp.setValue(blockPackageNames)
    }

    fun setUserMusic(index: Int) {
        val userMusicRef = FirebaseDatabase.getInstance().getReference("users/$index/music")

        userMusicRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val musicData = dataSnapshot.getValue(Music::class.java)
                    if (musicData != null) {
                        userMusic = musicData
                        Log.i("data", "Name: ${userMusic.name}, URL: ${userMusic.resourceId}")
                    } else {
                        Log.i("data", "No music data found")
                    }
                } else {
                    userMusic = Music("", 0)
                    Log.i("data", "No music data found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("data",databaseError.message)
            }
        })
    }

    fun getUserMusic(): Music{
        return userMusic
    }
    fun updateUserMusic(index: Int, music: Music) {
        val userMusic =  FirebaseDatabase.getInstance().getReference("users/$index/music")

        userMusic.setValue(music)
    }

    fun setUserStopwatch(index: Int) {
        val userStopwatchRef = FirebaseDatabase.getInstance().getReference("users/$index/stopwatch")

        userStopwatchRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempList = ArrayList<DateTime>()
                for (snapshot in dataSnapshot.children) {
                    val datetime = snapshot.getValue(DateTime::class.java)
                    datetime?.let {
                        tempList.add(it)
                    }
                }
                userStopwatch = tempList
                Log.i("firebase", userStopwatch.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error retrieving user info: ${databaseError.message}")
            }
        })
    }

    fun getUserStopwatch(): List<DateTime>{
        return userStopwatch
    }

    fun updateStopwatch(index: Int, newStopwatch: DateTime){
        val mutableStopwatchList = userStopwatch.toMutableList()

        mutableStopwatchList.add(newStopwatch)

        val userStopwatchRef =  FirebaseDatabase.getInstance().getReference("users/$index/stopwatch")
        userStopwatchRef.setValue(mutableStopwatchList.toList())
    }

    fun setUserPomodoro(index: Int) {
        val userPomodoroRef = FirebaseDatabase.getInstance().getReference("users/$index/pomodoro")

        userPomodoroRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempList = ArrayList<DateTime>()
                for (snapshot in dataSnapshot.children) {
                    val datetime = snapshot.getValue(DateTime::class.java)
                    datetime?.let {
                        tempList.add(it)
                    }
                }
                userPomodoro = tempList
                Log.i("firebase", userPomodoro.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error retrieving user info: ${databaseError.message}")
            }
        })
    }

    fun getUserPomodoro(): List<DateTime>{
        return userPomodoro
    }

    fun updatePomodoro(index: Int, newPomodoro: DateTime){
        val mutablePomodoroList = userPomodoro.toMutableList()

        mutablePomodoroList.add(newPomodoro)

        val userPomodoroRef =  FirebaseDatabase.getInstance().getReference("users/$index/pomodoro")
        userPomodoroRef.setValue(mutablePomodoroList.toList())
    }

    fun setUserReminder(index: Int) {
        val userReminderRef = FirebaseDatabase.getInstance().getReference("users/$index/reminder")

        userReminderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val reminderValue = dataSnapshot.getValue(Int::class.java)
                if (reminderValue != null) {
                    userReminder = reminderValue
                    Log.d("Firebase", "Reminder value: $reminderValue")
                } else {
                    Log.d("Firebase", "Reminder value is null")
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase",databaseError.message)
            }
        })
    }

    fun getUserReminder(): Int{
        return userReminder
    }
    fun updateUserReminder(index: Int, duration: Int) {
        val userReminder =  FirebaseDatabase.getInstance().getReference("users/$index/reminder")

        userReminder.setValue(duration)
    }

    fun addUserList(taskList: TaskList) {
        val newList = TaskList(
            taskList.id_list,
            taskList.list_color,
            taskList.list_icon,
            taskList.list_name
        )
        userListsRef.child(userList.size.toString()).setValue(newList)
    }

    fun countTasksOfList(listId: String): Int {
        return userTask.count { it.id_list == listId }
    }

    fun addNewTask(task: Task) {
        val newTask = Task(
            task.description,
            task.due_datetime,
            task.id_list,
            task.id_task,
            task.isDone,
            task.link,
            task.priority,
            task.title,
            task.reminder
        )
        userTasksRef.child(userTask.size.toString()).setValue(newTask)
    }

    fun updateTask(task: Task) {
        val newTask = Task(
            task.description,
            task.due_datetime,
            task.id_list,
            task.id_task,
            task.isDone,
            task.link,
            task.priority,
            task.title,
            task.reminder
        )

        userTasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (taskSnapshot in dataSnapshot.children) {
                        val userTask = taskSnapshot.getValue(Task::class.java)
                        if (userTask != null && userTask.id_task == task.id_task) {
                            taskSnapshot.ref.setValue(newTask)
                            return
                        }
                    }
                    Log.d("firebase", "Task with id ${task.id_task} not found")
                } else {
                    Log.d("firebase", "User task not found for UID")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("data",databaseError.message)
            }
        })
    }

    fun setUserPremium(index: Int) {
        val userPremiumRef = FirebaseDatabase.getInstance().getReference("users/$index/info/isPremium")
        userPremiumRef.setValue(true)
    }

    fun setTaskStatus(index: Int, taskId: String, isDone: Boolean) {
        // find task index that has taskId
        val task = userTask.find { it.id_task == taskId }
        val taskIndex = userTask.indexOf(task)
        val taskRef = FirebaseDatabase.getInstance().getReference("users/$index/tasks/$taskIndex/isDone")
        taskRef.setValue(isDone)
    }

    fun deleteTask(taskId: String) {
        userTasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (taskSnapshot in dataSnapshot.children) {
                        val userTask = taskSnapshot.getValue(Task::class.java)
                        if (userTask != null && userTask.id_task == taskId) {
                            taskSnapshot.ref.removeValue()
                            return
                        }
                    }
                    Log.d("UserTask", "Task with id $taskId not found")
                } else {
                    Log.d("UserTask", "User task not found for UID")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("data",databaseError.message)
            }
        })
    }

    fun updateTaskStatus(taskId: String, isDone: Boolean) {
        userTasksRef.addListenerForSingleValueEvent (object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (taskSnapshot in dataSnapshot.children) {
                        val userTask = taskSnapshot.getValue(Task::class.java)
                        if (userTask != null && userTask.id_task == taskId) {
                            taskSnapshot.ref.child("isDone").setValue(isDone)
                            return
                        }
                    }
                    Log.d("UserTask", "Task with id $taskId not found")
                } else {
                    Log.d("UserTask", "User task not found for UID")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("data",databaseError.message)
            }
        })
    }

    fun getHighPriorityUndoneTasks(): List<Task> {
        val tasks = userTask.filter { it.priority == "high" && !it.isDone }.sortedByDescending { it.due_datetime }
        return tasks
    }
}