package com.example.applepie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView =  inflater.inflate(R.layout.fragment_study_notification, container, false)

        backButton = rootView.findViewById(R.id.back_btn)
        appListView = rootView.findViewById(R.id.app_list_view)

        // TODO: get all music
//        val appList = getInstalledApps()
//        appListAdapter = AppListAdapter(appList, selectedApps, requireActivity().packageManager)
//        appListView.adapter = appListAdapter
        appListView.layoutManager = LinearLayoutManager(requireContext())

        backButton.setOnClickListener {
            previousRedFragment()
        }

        return rootView
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

//    private lateinit var appListAdapter: AppListAdapter
//    private val selectedApps = mutableListOf<String>()
    private lateinit var backButton: Button
    private lateinit var appListView: RecyclerView
}