package com.example.applepie

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    private lateinit var homeButton: Button
    private lateinit var studyButton: Button
    private lateinit var createTaskButton: Button
    private lateinit var reportButton: Button
    private lateinit var accountButton: Button

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
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI(view)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun handleNavbarClick(button: Button){

        // TODO: Change the button icon to @color/green

        // Change the button background
        homeButton.setBackgroundColor(Color.TRANSPARENT)
        studyButton.setBackgroundColor(Color.TRANSPARENT)
        createTaskButton.setBackgroundColor(Color.TRANSPARENT)
        reportButton.setBackgroundColor(Color.TRANSPARENT)
        accountButton.setBackgroundColor(Color.TRANSPARENT)

        button.background = resources.getDrawable(R.drawable.bg_button_active)
    }

    private fun setUI(view: View){
        homeButton = view.findViewById(R.id.home_icon)
        studyButton = view.findViewById(R.id.study_icon)
        createTaskButton = view.findViewById(R.id.create_task_icon)
        reportButton = view.findViewById(R.id.report_icon)
        accountButton = view.findViewById(R.id.account_icon)

        parentFragmentManager.beginTransaction().replace(R.id.fragment_container, Dashboard()).addToBackStack(null).commit()

        homeButton.setOnClickListener {
            handleNavbarClick(homeButton)
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, Dashboard()).addToBackStack(null).commit()
        }

        studyButton.setOnClickListener {
            handleNavbarClick(studyButton)
            if (!PomodoroTimer.isStop())
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, Pomodoro()).addToBackStack(null).commit()
            else
                parentFragmentManager.beginTransaction().replace(R.id.fragment_container, Stopwatch()).addToBackStack(null).commit()
        }

        createTaskButton.setOnClickListener {
            handleNavbarClick(createTaskButton)
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateTask()).addToBackStack(null).commit()
        }

        reportButton.setOnClickListener {
            handleNavbarClick(reportButton)
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, Report()).addToBackStack(null).commit()
        }

        accountButton.setOnClickListener {
            handleNavbarClick(accountButton)
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container, Account()).addToBackStack(null).commit()
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}