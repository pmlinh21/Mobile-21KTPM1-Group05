package com.example.applepie

import com.example.applepie.model.mail.EmailService
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.User
import com.example.applepie.model.mail.sendEmail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.mail.internet.InternetAddress
import kotlin.properties.Delegates

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
        setStyle(STYLE_NO_TITLE, R.style.customDialogFragment)
        emailET = view.findViewById(R.id.email_edit_text)
        confirmBtn = view.findViewById(R.id.confirm_button)

        confirmBtn.setOnClickListener {
            val email = emailET.text.toString()
            code = (100000..999999).random().toString()
            if (email.isNotBlank()) {
                val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                databaseReference.orderByChild("info/email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val userIndex = userSnapshot.key!!.toInt()

                                val user = userSnapshot.child("info").getValue(User::class.java)

                                if (user != null) {
                                    userId = userIndex
                                    sendEmail(email, code)
                                    buttonClicked = true
                                    dismiss()
                                } else {
                                    Toasty.error(requireContext(), "Email not found", Toast.LENGTH_SHORT, true).show()
                                }
                            }
                        } else {
                            Toasty.error(requireContext(), "Email not found", Toast.LENGTH_SHORT, true).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("EmailDialog", "Error getting data", error.toException())
                    }
                })
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
            val codeDialog = CodeDialog(emailET.text.toString(), code, userId!!)
            codeDialog.show(parentFragmentManager, "code_dialog")
        }
    }


    private var buttonClicked = false

    private var userId: Int? = null
    private lateinit var code: String

    private lateinit var emailET: EditText
    private lateinit var confirmBtn: Button
}