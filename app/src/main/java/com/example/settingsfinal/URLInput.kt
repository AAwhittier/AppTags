package com.example.settingsfinal

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class URLInput : AppCompatActivity() {
    object UrlInputConstants {
        const val MAX_BYTES = 504
        const val ACTIVITY_ID = "NFCUrl"
        const val INVALID_URL = "Please enter a valid URL."
    }
    private lateinit var urlText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_urlinput)
        supportActionBar?.hide()

        // Submits the URL to be written to the tag.
        val urlSubmitButton = findViewById<Button>(R.id.submit_url)
        urlSubmitButton.setOnClickListener {
            submitUrl()
        }
    }

    /**
     * Obtain the text value from the URL_input text box.
     */
    private fun getUrlText(): String {
        var text = findViewById<View>(R.id.URL_input) as EditText
        return text.text.toString()
    }

    /**
     * Validate that the URL is in a valid URL format.
     * String should be less than 504 bytes.
     */
    private fun validateURL(url: String): Boolean {
        // URLUtil Performs simple validation to ensure format is a URL, does not ensure the URL
        // itself exists.
        return if ((URLUtil.isValidUrl(url.lowercase())
            || Patterns.WEB_URL.matcher(url.lowercase()).matches())
            && url.toByteArray().size < UrlInputConstants.MAX_BYTES) {
            true
        } else {
            alertInvalidURL()
            false
        }
    }

    /**
     * Shows a pop up alert when a URL is invalid.
     */
    private fun alertInvalidURL() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(UrlInputConstants.INVALID_URL)
            .setPositiveButton("Confirm", DialogInterface.OnClickListener {
                    dialog, _ -> dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.show()
    }

    /**
     * Verify the text is a valid url ready for submitting to be written.
     */
    private fun submitUrl () {
        urlText = getUrlText()
        if (validateURL(urlText)) {
            val intent = Intent(this, WriteNFC::class.java)
            intent.putExtra("id", UrlInputConstants.ACTIVITY_ID)
            intent.putExtra("contents", urlText)
            startActivity(intent)
        }
    }
}