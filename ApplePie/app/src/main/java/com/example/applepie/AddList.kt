package com.example.applepie

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.TaskList
import com.example.applepie.model.User
import com.google.firebase.database.DatabaseError

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
    private lateinit var listIconButtons: MutableList<Button>

    private lateinit var chosenColorButton: Button
    private lateinit var chosenIconButton: Button

    private lateinit var preferenceManager: PreferenceManager

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

        preferenceManager = PreferenceManager(requireContext())

        setupUI(view)
        setupListColorButtons()
        setupListIconButtons()
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

        listIconButtons = mutableListOf()
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_1))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_2))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_3))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_4))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_5))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_6))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_7))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_8))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_9))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_10))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_11))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_12))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_13))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_14))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_15))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_16))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_17))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_18))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_19))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_20))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_21))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_22))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_23))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_24))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_25))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_26))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_27))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_28))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_29))
        listIconButtons.add(view.findViewById<Button>(R.id.list_icon_button_30))

        chosenColorButton = listColorButtons[3]
        chosenIconButton = listIconButtons[0]
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupDoneButton() {
        doneButton.setOnClickListener {
            // TODO: add list to database
            if (listNameET.text.isEmpty()) {
                Toast.makeText(context, "List name required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val newList = TaskList(
                    -1,
                    chosenColorButton.backgroundTintList!!.defaultColor,
                    chosenIconButton.text.toString(),
                    listNameET.text.toString()
                )
                FirebaseManager.addUserList(newList)
            }

            parentFragmentManager.popBackStack()
        }
    }

    private fun setupListColorButtons() {
        for (button in listColorButtons) {
            button.setOnClickListener {
                if (chosenColorButton != button) {
                    chosenColorButton.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle, null)
                    chosenColorButton.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
                    chosenColorButton = button
                    chosenColorButton.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle_border, null)
                    chosenColorButton.backgroundTintMode = PorterDuff.Mode.SCREEN
                    listIconTV.setTextColor(chosenColorButton.backgroundTintList)
                }
            }
        }
    }

    private fun setupListIconButtons() {
        for (button in listIconButtons) {
            button.setOnClickListener {
                if (chosenIconButton != button) {
                    chosenIconButton.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle, null)
                    chosenIconButton.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
                    chosenIconButton = button
                    chosenIconButton.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle_border, null)
                    chosenIconButton.backgroundTintMode = PorterDuff.Mode.SCREEN
                    listIconTV.text = chosenIconButton.text
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