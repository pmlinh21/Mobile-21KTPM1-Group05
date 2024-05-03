package com.example.applepie

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StudySetting.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudySetting : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_study_setting, container, false)

        notificationButton = rootView.findViewById(R.id.notification_btn)
        whitenoiseButton = rootView.findViewById(R.id.whitenoise_btn)
        soundmusicSwitch = rootView.findViewById(R.id.soundmusic_switch)
        backButton = rootView.findViewById(R.id.back_btn)

        val preferenceManager = PreferenceManager(requireContext())
        soundmusicSwitch.isChecked = preferenceManager.getMusicStatus() == true
        controlMusic(soundmusicSwitch.isChecked, preferenceManager)

        notificationButton.setOnClickListener {
            // TODO: show list app to manage noti
            if (FirebaseManager.getUserInfo().isPremium) {
                (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudyNotification()).addToBackStack("study_notification").commit()
            } else {
                val subscribeActivity = Intent(this.activity, SubscribeActivity::class.java)
                startActivity(subscribeActivity)
            }
        }

        whitenoiseButton.setOnClickListener {
            // TODO:  show list music
            if (FirebaseManager.getUserInfo().isPremium) {
                (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudyMusic()).addToBackStack("study_white_noise").commit()
            } else {
                val subscribeActivity = Intent(this.activity, SubscribeActivity::class.java)
                startActivity(subscribeActivity)
            }
        }

        soundmusicSwitch.setOnCheckedChangeListener { _, isChecked ->
            controlMusic(isChecked, preferenceManager)
        }

        backButton.setOnClickListener {
            previousRedFragment()
        }

        return rootView
    }

    private fun controlMusic(isChecked: Boolean, preferenceManager: PreferenceManager){
        Log.i("music", isChecked.toString())
        if (isChecked) {
            // Turn on music
            if (PomodoroTimer.isStarted() || StopwatchTimer.isStarted()){
                val playIntent = Intent(context, MusicService::class.java).apply {
                    putExtra("ACTION", "PLAY")
                }
                context?.startService(playIntent)
            }
            preferenceManager.setMusicStatus(true)
        } else {
            // Turn off music
            val pauseIntent = Intent(context, MusicService::class.java).apply {
                putExtra("ACTION", "PAUSE")
            }
            context?.startService(pauseIntent)

            preferenceManager.setMusicStatus(false)
        }
    }

    private fun startSoundMusic(){
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.music_1)
            mediaPlayer?.apply {
                isLooping = true
                setOnErrorListener { mp, what, extra ->
                    Log.e("MediaPlayer", "Error occurred: What $what, Extra $extra")
                    true
                }
                setOnPreparedListener {
                    start()
                }
            }
        } else {
            mediaPlayer?.start()
        }
    }

    private fun stopSoundMusic() {
        mediaPlayer?.pause() // This will pause the media player
    }

    private fun previousRedFragment(){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val fragmentManager = activity?.supportFragmentManager
        val count = fragmentManager!!.backStackEntryCount
        fragmentManager.popBackStackImmediate()
        transaction?.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StudySetting.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StudySetting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private lateinit var notificationButton: Button

    private val audioManager: AudioManager by lazy {
        val context = requireContext()
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var whitenoiseButton: Button
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var soundmusicSwitch: Switch
    private lateinit var backButton: Button
}