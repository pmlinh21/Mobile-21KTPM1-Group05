package com.example.applepie

import android.os.Handler
import android.os.Looper
import android.util.Log

object StopwatchTimer {

    private var secondsElapsed: Int = 0
    private var handler: Handler? = null
    private var updateRunnable: Runnable? = null
    private var isPaused: Boolean = false
    private var isStarted: Boolean = false

    // Start the timer
    fun startTimer() {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
            updateRunnable = object : Runnable {
                override fun run() {
                    if (!isPaused) {
                        secondsElapsed++
                        updateTimeTextCallback?.invoke() // Update text here
                    }

                    handler?.postDelayed(this, 1000)
                }
            }
            handler?.post(updateRunnable!!)
            isStarted = true
            Log.i("Timer", "Timer started")
        } else{
            Log.i("Timer", "Timer already running")
        }
        if (isStarted)
            updateButtonCallback?.invoke()
    }

    fun isPause(): Boolean{
        return isPaused && isStarted
    }

    fun isRunning(): Boolean{
        return !isPaused && isStarted
    }

    fun isStarted(): Boolean{
        return isStarted
    }

    fun isStop(): Boolean{
        return !isStarted
    }

    // Pause the timer
    fun pauseTimer() {
        isPaused = true
        updateButtonCallback?.invoke()
    }

    // Resume the timer
    fun resumeTimer() {
        isPaused = false
    }

    // Stop the timer
    fun stopTimer() {
        isPaused = false
        isStarted = false
        secondsElapsed = 0

        updateTimeTextCallback?.invoke()
        updateButtonCallback?.invoke()

        updateRunnable?.let { handler?.removeCallbacks(it) }
        handler = null

    }

    // Format the elapsed time in hh:mm:ss format
    fun getFormattedTime(): String {
        val hours = secondsElapsed / 3600
        val minutes = secondsElapsed % 3600 / 60
        val seconds = secondsElapsed % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Set up callback for updating UI
    private var updateTimeTextCallback: (() -> Unit)? = null
    private var updateButtonCallback: (() -> Unit)? = null
    fun setUpdateTimeTextCallback(callback: () -> Unit) {
        updateTimeTextCallback = callback
    }

    fun setUpdateButtonTextCallback(callback: () -> Unit) {
        updateButtonCallback = callback
    }
}
