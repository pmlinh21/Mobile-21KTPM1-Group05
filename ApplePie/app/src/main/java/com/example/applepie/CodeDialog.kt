package com.example.applepie

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.mail.EmailService
import com.example.applepie.model.mail.sendEmail
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.mail.internet.InternetAddress


class CodeDialog(private val email: String, private val code: String, private val userId: Int) : DialogFragment() {

    // The system calls this to get the DialogFragment's layout, regardless of
    // whether it's being displayed as a dialog or an embedded fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as a dialog or embedded fragment.
        val view = inflater.inflate(R.layout.layout_code, container, false)
        code1 = view.findViewById(R.id.code_1)
        code2 = view.findViewById(R.id.code_2)
        code3 = view.findViewById(R.id.code_3)
        code4 = view.findViewById(R.id.code_4)
        code5 = view.findViewById(R.id.code_5)
        code6 = view.findViewById(R.id.code_6)

        verifyBtn = view.findViewById(R.id.verify_button)
        resendCode = view.findViewById(R.id.resend_code_button)

        code1.doAfterTextChanged { code2.requestFocus() }
        code2.doAfterTextChanged { code3.requestFocus() }
        code3.doAfterTextChanged { code4.requestFocus() }
        code4.doAfterTextChanged { code5.requestFocus() }
        code5.doAfterTextChanged { code6.requestFocus() }

        // backspace listener

        code2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL && code2.text.toString().isEmpty()) {

                code1.requestFocus()
                code1.text.clear()
            }
            false
        }
        code3.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL && code3.text.toString().isEmpty()) {

                code2.requestFocus()
                code2.text.clear()
            }
            false
        }
        code4.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL && code4.text.toString().isEmpty()) {

                code3.requestFocus()
                code3.text.clear()
            }
            false
        }
        code5.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL && code5.text.toString().isEmpty()) {

                code4.requestFocus()
                code4.text.clear()
            }
            false
        }
        code6.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL && code6.text.toString().isEmpty()) {

                code5.requestFocus()
                code5.text.clear()
            }
            false
        }

        verifyBtn.setOnClickListener {
            val inputCode = code1.text.toString() + code2.text.toString() + code3.text.toString() + code4.text.toString() + code5.text.toString() + code6.text.toString()
            if (inputCode == code) {
                buttonClicked = true
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_SHORT).show()
            }
        }

        resendCode.setOnClickListener {
            resendCode.isEnabled = false
            Toasty.info(requireContext(), "Resending code...", Toast.LENGTH_SHORT, true).show()
            sendEmail(email, code)
        }
        isCancelable = false
        return view
    }

    // The system calls this only when creating the layout in a dialog.
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (buttonClicked) {
            val passwordDialog = PasswordDialog(userId)
            passwordDialog.show(parentFragmentManager, "password_dialog")
        }
    }

    private lateinit var code1: EditText
    private lateinit var code2: EditText
    private lateinit var code3: EditText
    private lateinit var code4: EditText
    private lateinit var code5: EditText
    private lateinit var code6: EditText

    private lateinit var verifyBtn: Button
    private lateinit var resendCode: Button

    private var buttonClicked = false
}