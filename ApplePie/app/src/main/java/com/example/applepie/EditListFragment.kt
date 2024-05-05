package com.example.applepie

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.text.RelativeDateTimeFormatter.RelativeDateTimeUnit
import android.os.Bundle
import android.util.Log
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

class EditListFragment(val list: TaskList) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_list, container, false)
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

        chosenColorButton = when(list.list_color) {
            listColorButtons[0].backgroundTintList!!.defaultColor -> listColorButtons[0]
            listColorButtons[1].backgroundTintList!!.defaultColor -> listColorButtons[1]
            listColorButtons[2].backgroundTintList!!.defaultColor -> listColorButtons[2]
            listColorButtons[3].backgroundTintList!!.defaultColor -> listColorButtons[3]
            listColorButtons[4].backgroundTintList!!.defaultColor -> listColorButtons[4]
            listColorButtons[5].backgroundTintList!!.defaultColor -> listColorButtons[5]
            listColorButtons[6].backgroundTintList!!.defaultColor -> listColorButtons[6]
            listColorButtons[7].backgroundTintList!!.defaultColor -> listColorButtons[7]
            listColorButtons[8].backgroundTintList!!.defaultColor -> listColorButtons[8]
            listColorButtons[9].backgroundTintList!!.defaultColor -> listColorButtons[9]
            listColorButtons[10].backgroundTintList!!.defaultColor -> listColorButtons[10]
            listColorButtons[11].backgroundTintList!!.defaultColor -> listColorButtons[11]
            else -> null
        }
        chosenIconButton = when(list.list_icon) {
            listIconButtons[0].text -> listIconButtons[0]
            listIconButtons[1].text -> listIconButtons[1]
            listIconButtons[2].text -> listIconButtons[2]
            listIconButtons[3].text -> listIconButtons[3]
            listIconButtons[4].text -> listIconButtons[4]
            listIconButtons[5].text -> listIconButtons[5]
            listIconButtons[6].text -> listIconButtons[6]
            listIconButtons[7].text -> listIconButtons[7]
            listIconButtons[8].text -> listIconButtons[8]
            listIconButtons[9].text -> listIconButtons[9]
            listIconButtons[10].text -> listIconButtons[10]
            listIconButtons[11].text -> listIconButtons[11]
            listIconButtons[12].text -> listIconButtons[12]
            listIconButtons[13].text -> listIconButtons[13]
            listIconButtons[14].text -> listIconButtons[14]
            listIconButtons[15].text -> listIconButtons[15]
            listIconButtons[16].text -> listIconButtons[16]
            listIconButtons[17].text -> listIconButtons[17]
            listIconButtons[18].text -> listIconButtons[18]
            listIconButtons[19].text -> listIconButtons[19]
            listIconButtons[20].text -> listIconButtons[20]
            listIconButtons[21].text -> listIconButtons[21]
            listIconButtons[22].text -> listIconButtons[22]
            listIconButtons[23].text -> listIconButtons[23]
            listIconButtons[24].text -> listIconButtons[24]
            listIconButtons[25].text -> listIconButtons[25]
            listIconButtons[26].text -> listIconButtons[26]
            listIconButtons[27].text -> listIconButtons[27]
            listIconButtons[28].text -> listIconButtons[28]
            listIconButtons[29].text -> listIconButtons[29]
            else -> null
        }

        listNameET.setText(list.list_name)
        listIconTV.text = list.list_icon
        listIconTV.setTextColor(list.list_color - 0x1000000)
        if (chosenColorButton != null) {
            chosenColorButton!!.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle_border, null)
            chosenColorButton!!.backgroundTintMode = PorterDuff.Mode.SCREEN
        }
        if (chosenIconButton != null) {
            chosenIconButton!!.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle_border, null)
            chosenIconButton!!.backgroundTintMode = PorterDuff.Mode.SCREEN
        }
    }

    private fun setupBackButton() {
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupDoneButton() {
        doneButton.setOnClickListener {
            // TODO: add list to database
            if (listNameET.text.isEmpty()) {
                Toast.makeText(context, "List name required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (list.list_color == chosenColorButton?.backgroundTintList!!.defaultColor && list.list_icon == chosenIconButton?.text.toString() && list.list_name == listNameET.text.toString()) {
                dismiss()
                return@setOnClickListener
            }
            else {
                val editList = TaskList(
                    LocalDateTime.now().toString().replace("-", "").replace("T", "").replace(":", "").split(".")[0],
                    chosenColorButton?.backgroundTintList!!.defaultColor ?: list.list_color,
                    chosenIconButton?.text.toString() ?: list.list_icon,
                    listNameET.text.toString()
                )
                FirebaseManager.editList(editList)
            }

            dismiss()
        }
    }

    private fun setupListColorButtons() {
        for (button in listColorButtons) {
            button.setOnClickListener {
                if (chosenColorButton != null && chosenColorButton != button) {
                    chosenColorButton!!.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle, null)
                    chosenColorButton!!.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
                    chosenColorButton = button
                    chosenColorButton!!.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle_border, null)
                    chosenColorButton!!.backgroundTintMode = PorterDuff.Mode.SCREEN
                    listIconTV.setTextColor(chosenColorButton!!.backgroundTintList)
                }
            }
        }
    }

    private fun setupListIconButtons() {
        for (button in listIconButtons) {
            button.setOnClickListener {
                if (chosenIconButton != null && chosenIconButton != button) {
                    chosenIconButton!!.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle, null)
                    chosenIconButton!!.backgroundTintMode = PorterDuff.Mode.SRC_ATOP
                    chosenIconButton = button
                    chosenIconButton!!.background = ResourcesCompat.getDrawable(resources, R.drawable.shape_circle_border, null)
                    chosenIconButton!!.backgroundTintMode = PorterDuff.Mode.SCREEN
                    listIconTV.text = chosenIconButton!!.text
                }
            }
        }
    }

    private lateinit var cancelButton: Button
    private lateinit var doneButton: Button

    private lateinit var listIconTV: TextView
    private lateinit var listNameET: EditText
    private lateinit var listColorButtons: MutableList<Button>
    private lateinit var listIconButtons: MutableList<Button>

    private var chosenColorButton: Button? = null
    private var chosenIconButton: Button? = null

    private lateinit var preferenceManager: PreferenceManager
}