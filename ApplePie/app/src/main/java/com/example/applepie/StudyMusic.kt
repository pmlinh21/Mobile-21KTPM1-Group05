package com.example.applepie

import MusicListAdapter
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.Music

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StudyMusic.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudyMusic : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    selectedMusic = FirebaseManager.getUserMusic()
    handler = Handler()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_study_music, container, false)

        backButton = rootView.findViewById(R.id.back_btn)
        musicListView = rootView.findViewById(R.id.music_list_view)

        // TODO: get all music
        val musicList = getAllRawSounds(requireContext())
        musicListAdapter = MusicListAdapter(musicList, selectedMusic)
        musicListView.adapter = musicListAdapter
        musicListView.layoutManager = LinearLayoutManager(requireContext())

        musicListAdapter.setOnItemClickListener { position ->


            selectedMusic = musicList[position]
            Log.i("MusicListAdapter", selectedMusic.toString())

            stopPlayback()

            mediaPlayer = MediaPlayer.create(requireContext(), selectedMusic.resourceId)
            mediaPlayer?.start()

            stopPlaybackRunnable = Runnable { stopPlayback() }
            handler?.postDelayed(stopPlaybackRunnable!!, 8000) // 10 seconds in milliseconds
        }


        backButton.setOnClickListener {
            updateMusic()
            stopPlayback()
            previousRedFragment()
        }

        return rootView
    }

    fun getAllRawSounds(context: Context): List<Music> {
        val rawSounds: MutableList<Music> = mutableListOf()

        // Get the list of resource IDs in the R.raw class using reflection
        val rawClass = R.raw::class.java
        val fields = rawClass.fields
        for (field in fields) {
            val fieldName = field.name
            val fieldResourceType = field.type

            // Check if the field type is int (resource ID)
            if (fieldResourceType == Int::class.java) {
                val resourceId = field.getInt(null)
                val displayName = formatText(context.resources.getResourceEntryName(resourceId))

                rawSounds.add(Music(displayName, resourceId))
            }
        }

        Log.i("music",rawSounds.toString())

        return rawSounds
    }

    private fun formatText(input: String): String {
        val words = input.split("_") // Split the input string by underscores
        val formattedWords = words.map { it.capitalize() } // Capitalize each word
        return formattedWords.joinToString(" ") // Join the words back together with a space
    }

    private fun updateMusic(){
        FirebaseManager.updateUserMusic(0, selectedMusic)
    }
    private fun previousRedFragment(){
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        val fragmentManager = activity?.supportFragmentManager
        val count = fragmentManager!!.backStackEntryCount
        fragmentManager.popBackStackImmediate()
        transaction?.commit()
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        stopPlaybackRunnable?.let { handler?.removeCallbacks(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Release resources when the fragment is destroyed
        mediaPlayer?.release()
        handler?.removeCallbacksAndMessages(null)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StudyMusic.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StudyMusic().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private var stopPlaybackRunnable: Runnable? = null

    private lateinit var selectedMusic: Music
    private lateinit var musicListAdapter: MusicListAdapter

    private lateinit var backButton: Button
    private lateinit var musicListView: RecyclerView
}