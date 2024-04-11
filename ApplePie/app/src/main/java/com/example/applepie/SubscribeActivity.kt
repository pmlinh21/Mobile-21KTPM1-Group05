package com.example.applepie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SubscribeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe)

        val intent = intent
        closeButton = findViewById<Button>(R.id.close_button)

        handleEventListener()
    }

    private fun handleEventListener() {
        closeButton.setOnClickListener {
            finish()
        }
    }

    private lateinit var closeButton: Button
}