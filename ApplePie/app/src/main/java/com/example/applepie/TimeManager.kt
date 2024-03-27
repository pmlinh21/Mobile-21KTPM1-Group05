package com.example.applepie

import android.os.Handler
import java.util.Timer
import java.util.TimerTask

class TimerManager {

    private var timer: Timer? = null
    private var secondsElapsed: Int = 0
    private var handler: Handler? = null
    private var updateRunnable: Runnable? = null
    private var isPaused: Boolean = false

    // Start the timer
    fun startTimer() {
        if (timer == null) {
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (!isPaused) {
                        secondsElapsed++
                        handler?.post(updateRunnable!!)
                    }
                }
            }, 0, 1000)
        }
    }

    fun getPause(): Boolean{
        return isPaused
    }

    // Pause the timer
    fun pauseTimer() {
        isPaused = true
    }

    // Resume the timer
    fun resumeTimer() {
        isPaused = false
    }

    // Stop the timer
    fun stopTimer() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    // Reset the timer
    fun resetTimer() {
        secondsElapsed = 0
    }

    // Format the elapsed time in hh:mm:ss format
    fun getFormattedTime(): String {
        val hours = secondsElapsed / 3600
        val minutes = secondsElapsed % 3600 / 60
        val seconds = secondsElapsed % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Set up handler and runnable for updating UI
    fun setUpdateCallback(updateCallback: () -> Unit) {
        handler = Handler()
        updateRunnable = Runnable { updateCallback() }
    }
}