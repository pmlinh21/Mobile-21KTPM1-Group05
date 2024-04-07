package com.example.applepie

import android.os.Handler
import android.os.Looper
import android.util.Log

object StopwatchTimer {

    private var secondsElapsed: Int = 0
    private var handler: Handler? = null
    private var updateRunnable: Runnable? = null
    private var isPaused: Boolean = false

    // Start the timer
    fun startTimer() {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
            updateRunnable = object : Runnable {
                override fun run() {
                    if (!isPaused) {
                        secondsElapsed++
                        updateCallback?.invoke() // Invoke the callback to update UI
                    }
                    handler?.postDelayed(this, 1000) // Update every second
                }
            }
            handler?.post(updateRunnable!!)
            Log.i("Timer", "Timer started")
        } else{
            Log.i("Timer", "Timer already running")
        }
    }

    fun isPause(): Boolean{
//        Log.i("secondsElapsed1",secondsElapsed.toString())
        return isPaused && secondsElapsed != 0
    }

    fun isRunning(): Boolean{
//        Log.i("secondsElapsed2",secondsElapsed.toString())
        return !isPaused && secondsElapsed != 0
    }

    fun isStop(): Boolean{
//        Log.i("secondsElapsed3",secondsElapsed.toString())
        return secondsElapsed == 0
    }

    // Pause the timer
    fun pauseTimer() {
        isPaused = true
        startTimer()
    }

    // Resume the timer
    fun resumeTimer() {
        isPaused = false
    }

    // Stop the timer
    fun stopTimer() {
        updateRunnable?.let { handler?.removeCallbacks(it) }
        handler = null
        secondsElapsed = 0
        isPaused = false
    }

    // Reset the timer
    fun resetTimer() {
        secondsElapsed = 0
        updateCallback?.invoke()
    }

    // Format the elapsed time in hh:mm:ss format
    fun getFormattedTime(): String {
        val hours = secondsElapsed / 3600
        val minutes = secondsElapsed % 3600 / 60
        val seconds = secondsElapsed % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Set up callback for updating UI
    private var updateCallback: (() -> Unit)? = null
    fun setUpdateCallback(callback: () -> Unit) {
        updateCallback = callback
    }
}
