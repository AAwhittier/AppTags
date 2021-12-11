package com.example.settingsfinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class TextInput : AppCompatActivity() {
    object TextInputConstants {
        const val MAX_BYTES = 504
        const val ACTIVITY_ID = "NFCText"
    }
    private lateinit var text: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_input)
        supportActionBar?.hide()

        // Go to activity to input text for the tag.
        val textSubmitButton = findViewById<Button>(R.id.submit_message)
        textSubmitButton.setOnClickListener {
            submitText()
        }
    }

    /**
     * Obtain the text value from the NFC_Message text box.
     */
    private fun getText(): String {
        var text = findViewById<View>(R.id.NFC_Message) as EditText
        return text.text.toString()
    }

    /**
     * Validates the text in the NFC_Message box.
     * Text should be smaller than 504 bytes. (504 bytes available on NTAG 215)
     */
    private fun validateText(text: String): Boolean {
        return text.toByteArray().size < TextInputConstants.MAX_BYTES
    }

    /**
     * Prepare the intent with the text from NFC_Message to be written.
     */
    private fun submitText () {
        text = getText()
        if (validateText(text)) {
            val intent = Intent(this, WriteNFC::class.java)
            intent.putExtra("id", TextInputConstants.ACTIVITY_ID)
            intent.putExtra("contents", text)
            startActivity(intent)
        }
    }
}