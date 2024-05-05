package com.example.applepie

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.example.applepie.database.FirebaseManager
import es.dmoral.toasty.Toasty


class PasswordDialog(private val userId: Int) : DialogFragment() {

    // The system calls this to get the DialogFragment's layout, regardless of
    // whether it's being displayed as a dialog or an embedded fragment.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as a dialog or embedded fragment.
        val view = inflater.inflate(R.layout.layout_password, container, false)

        passwordET = view.findViewById(R.id.password_edit_text)
        confirmPasswordET = view.findViewById(R.id.password_confirm_edit_text)
        confirmBtn = view.findViewById(R.id.confirm_button)

        confirmBtn.setOnClickListener {
            val password = passwordET.text.toString()
            val confirmPassword = confirmPasswordET.text.toString()
            if (password.isNotBlank() && password == confirmPassword) {
                FirebaseManager.setUserPassword(userId, password)
                Toasty.success(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT, true).show()
                dismiss()
            } else {
                Toasty.error(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT, true).show()
            }
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

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Toasty.error(requireContext(), "Please enter your new password", Toast.LENGTH_SHORT, true).show()
    }

    private lateinit var passwordET: EditText
    private lateinit var confirmPasswordET: EditText
    private lateinit var confirmBtn: Button
}