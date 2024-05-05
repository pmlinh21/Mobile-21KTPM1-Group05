package com.example.applepie

import com.example.applepie.model.mail.EmailService
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.applepie.model.mail.sendEmail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.mail.internet.InternetAddress

class EmailDialog : DialogFragment() {

    // The system calls this to get the DialogFragment's layout, regardless of
    // whether it's being displayed as a dialog or an embedded fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as a dialog or embedded fragment.
        val view = inflater.inflate(R.layout.layout_email, container, false)
        emailET = view.findViewById(R.id.email_edit_text)
        confirmBtn = view.findViewById(R.id.confirm_button)

        confirmBtn.setOnClickListener {
            val email = emailET.text.toString()
            code = (100000..999999).random().toString()
            if (email.isNotBlank()) {
                sendEmail(email, code)
                buttonClicked = true
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Please enter an email", Toast.LENGTH_SHORT).show()
            }
        }

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
            val codeDialog = CodeDialog(emailET.text.toString(), code)
            codeDialog.show(parentFragmentManager, "code_dialog")
        }
    }

    private var buttonClicked = false
    private lateinit var code: String

    private lateinit var emailET: EditText
    private lateinit var confirmBtn: Button
}