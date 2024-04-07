import android.os.Handler

class PomodoroTimer {

    private var minute: Int = 25
    private var secondsRemaining: Int = minute * 60 // 25 minutes initially
    private var handler: Handler? = null
    private var updateRunnable: Runnable? = null
    private var timerRunning: Boolean = false
    private var updateCallback: (() -> Unit)? = null

    // Start the timer
    fun startTimer() {
        if (!timerRunning) {
            timerRunning = true
            handler = Handler()
            updateRunnable = object : Runnable {
                override fun run() {
                    if (secondsRemaining > 0) {
                        secondsRemaining--
                        handler?.postDelayed(this, 1000) // Update every second
                        updateCallback?.invoke() // Invoke the callback to update UI
                    } else {
                        timerRunning = false
                    }
                }
            }
            handler?.post(updateRunnable!!)
        }
    }

    fun setMinute(minute: Int){
        this.minute = minute
    }
    fun getPause(): Boolean{
        return !timerRunning
    }

    // Pause the timer
    fun pauseTimer() {
        updateRunnable?.let { handler?.removeCallbacks(it) }
        timerRunning = false
    }

    // Reset the timer
    fun resetTimer() {
        secondsRemaining = minute * 60 // Reset to 25 minutes
        updateCallback?.invoke() // Update UI after reset
    }

    // Format the remaining time in mm:ss format
    fun getFormattedTime(): String {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Set up callback for updating UI
    fun setUpdateCallback(callback: () -> Unit) {
        this.updateCallback = callback
    }
}
