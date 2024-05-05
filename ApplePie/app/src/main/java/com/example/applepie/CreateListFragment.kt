package com.example.applepie

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit
import android.os.Bundle
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.time.LocalDateTime

class CreateListFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet: View = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR;
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT);
        bottomSheet.setBackgroundColor(Color.TRANSPARENT);

        preferenceManager = PreferenceManager(requireContext())

        setupUI(view)
        setupListColorButtons()
        setupListIconButtons()
        setupBackButton()
        setupDoneButton()
    }

    private fun setupUI(view: View) {
        cancelButton = view.findViewById<Button>(R.id.cancel_button)
        addButton = view.findViewById<Button>(R.id.add_button)

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
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupDoneButton() {
        addButton.setOnClickListener {
            // TODO: add list to database
            if (listNameET.text.isEmpty()) {
                Toast.makeText(context, "List name required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val newList = TaskList(
                    LocalDateTime.now().toString().replace("-", "").replace("T", "").replace(":", "").split(".")[0],
                    chosenColorButton.backgroundTintList!!.defaultColor,
                    chosenIconButton.text.toString(),
                    listNameET.text.toString()
                )
                FirebaseManager.addList(newList)
            }

            dismiss()
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

    private lateinit var cancelButton: Button
    private lateinit var addButton: Button

    private lateinit var listIconTV: TextView
    private lateinit var listNameET: EditText
    private lateinit var listColorButtons: MutableList<Button>
    private lateinit var listIconButtons: MutableList<Button>

    private lateinit var chosenColorButton: Button
    private lateinit var chosenIconButton: Button

    private lateinit var preferenceManager: PreferenceManager
}