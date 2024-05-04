import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.applepie.StopwatchTimer

object PomodoroTimer {
    private const val STUDY_MINUTE = 5
    private const val BREAK_MINUTE = 1

    private const val STUDY_MODE = "Focus"
    private const val BREAK_MODE = "Break"

    private var mode: String = STUDY_MODE
    private var minute: Int = STUDY_MINUTE
    private var secondsRemaining: Int = minute * 60 // 25 minutes initially
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
                    if (secondsRemaining > 0 && !isPaused) {
                        secondsRemaining--
                        updateTimeTextCallback?.invoke() // Invoke the callback to update UI
                    }
                    else if (secondsRemaining == 0){
                        secondsRemaining--
                        updateButtonCallback?.invoke()
                    }
                    handler?.postDelayed(this, 1000) // Update every second
                }
            }
            handler?.post(updateRunnable!!)
            isStarted = true
        }
        if (isStarted)
            updateButtonCallback?.invoke()
    }

    fun getMode(): String{
        return mode
    }
    fun switchMinute(){
        if (minute == STUDY_MINUTE){
            minute = BREAK_MINUTE
            mode = BREAK_MODE
        }
        else{
            minute = STUDY_MINUTE
            mode = STUDY_MODE
        }

        updateModeCallback?.invoke()
    }
    fun isPause(): Boolean{
        return isPaused && isStarted
    }

    fun isRunning(): Boolean{
        return !isPaused && isStarted
    }

    fun isStop(): Boolean{
        return !isStarted
    }

    fun isStarted(): Boolean{
        return isStarted && secondsRemaining > 0
    }

    fun isTimeOver(): Boolean{
        Log.i("pomodoro", secondsRemaining.toString())
        return isStarted && !isPaused && secondsRemaining < 0
    }

    // Pause the timer
    fun pauseTimer() {
        isPaused = true
        updateButtonCallback?.invoke()
    }

    fun resumeTimer() {
        isPaused = false
    }

    // Reset the timer
    fun stopTimer() {
        isPaused = false
        isStarted = false
        secondsRemaining = minute * 60

        updateTimeTextCallback?.invoke()
        updateButtonCallback?.invoke()

        updateRunnable?.let { handler?.removeCallbacks(it) }
        handler = null
    }

    fun getProgressValue(): Int{
        Log.i("timer", (secondsRemaining * 100 / (minute * 60)).toString())
        return secondsRemaining * 100 / (minute * 60)
    }

    fun getFormattedTime(): String {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private var updateTimeTextCallback: (() -> Unit)? = null
    private var updateButtonCallback: (() -> Unit)? = null
    private var updateModeCallback: (() -> Unit)? = null
    fun setUpdateTimeTextCallback(callback: () -> Unit) {
        this.updateTimeTextCallback = callback
    }

    fun setUpdateModeTextCallback(callback: () -> Unit) {
        this.updateModeCallback = callback
    }

    fun setUpdateButtonCallback(callback: () -> Unit) {
        this.updateButtonCallback = callback
    }
}
