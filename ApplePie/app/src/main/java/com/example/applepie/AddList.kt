package com.example.applepie

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddList.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddList : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var backButton: Button
    private lateinit var doneButton: Button

    private lateinit var listIconTV: TextView
    private lateinit var listNameET: EditText
    private lateinit var listColorButtons: MutableList<Button>

    private lateinit var chosenColorButton: Button
    private lateinit var listColors: MutableList<Int>
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
        return inflater.inflate(R.layout.fragment_add_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)
        setupListColorButtons()
        setupBackButton()
        setupDoneButton()
    }

    private fun setupUI(view: View) {
        backButton = view.findViewById<Button>(R.id.back_button)
        doneButton = view.findViewById<Button>(R.id.done_button)

        listIconTV = view.findViewById<TextView>(R.id.list_icon_text_view)
        listNameET = view.findViewById<EditText>(R.id.list_name_edit_text)

        listColorButtons = mutableListOf()
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_1))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_2))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_3))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_4))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_5))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_6))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_7))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_8))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_9))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_10))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_11))
        listColorButtons.add(view.findViewById<Button>(R.id.list_color_button_12))

        listColors = mutableListOf()
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_1, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_2, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_3, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_4, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_5, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_6, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_7, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_8, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_9, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_10, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_11, null))
        listColors.add(ResourcesCompat.getColor(resources, R.color.list_color_12, null))

        chosenColorButton = listColorButtons[3]

    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupDoneButton() {
        doneButton.setOnClickListener {
            // TODO: add list to database
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupListColorButtons() {
        for (button in listColorButtons) {
            button.setOnClickListener {
                if (chosenColorButton != button) {
                    chosenColorButton.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle, null)
                    chosenColorButton = button
                    chosenColorButton.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle_border, null)
                    listIconTV.setTextColor(listColors[listColorButtons.indexOf(button)])
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddList.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddList().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}