package com.example.applepie

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "MusicPlaybackChannel"

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(this, R.raw.music_1).apply {
            isLooping = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra("ACTION")
        var musicResId = intent?.getIntExtra("MUSIC_RES_ID", 2131820550) ?: 2131820550
        if (musicResId == 0)
            musicResId = 2131820550
        Log.d("MusicService", "Action: $action, Music Resource ID: $musicResId")
        when (action) {
            "PLAY" -> playMusic(musicResId)
            "PAUSE" -> pauseMusic()
            "STOP" -> stopSelf()
        }

        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playMusic(musicResId: Int) {
        if (!requestAudioFocus()) {
            Log.e("MusicService", "Failed to gain audio focus")
            return
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, musicResId)
            if (mediaPlayer == null) {
                Log.e("MusicService", "MediaPlayer failed to initialize")
                return
            }
            mediaPlayer?.apply {
                isLooping = true
                setOnPreparedListener { start() }
                setOnErrorListener { mp, what, extra ->
                    Log.e("MusicService", "MediaPlayer error: what $what, extra $extra")
                    true
                }
                prepareAsync() // Ensure you prepare the MediaPlayer
            }
            startForegroundService()
        } else {
            mediaPlayer?.apply {
                reset() // Reset the MediaPlayer to its uninitialized state
                val afd = resources.openRawResourceFd(musicResId)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepareAsync() // Prepare the MediaPlayer asynchronously
                setOnPreparedListener { start() } // Start playback once preparation is done
            }
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Playing")
            .setContentText("Your app name")
            .setSmallIcon(R.drawable.apple_pie_half)
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocus(): Boolean {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setOnAudioFocusChangeListener { focusChange ->
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        mediaPlayer?.pause()
                    }
                }
                build()
            }
        )
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }
}