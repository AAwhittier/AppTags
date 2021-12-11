package com.example.settingsfinal

import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        checkNfcSettings()

        // Go to activity to select an application.
        val appSelectButton = findViewById<Button>(R.id.app_select_button)
        appSelectButton.setOnClickListener {
            val intent = Intent(this, AppSelector::class.java)
            startActivity(intent)
        }

        // Go to activity to input a URL for the tag.
        val urlInputButton = findViewById<Button>(R.id.url_button)
        urlInputButton.setOnClickListener {
            val intent = Intent(this, URLInput::class.java)
            startActivity(intent)
        }

        // Go to activity to input text for the tag.
        val textInputButton = findViewById<Button>(R.id.text_button)
        textInputButton.setOnClickListener {
            val intent = Intent(this, TextInput::class.java)
            startActivity(intent)
        }
    }

    /**
     * Check if the device currently has NFC enabled.
     * Opens settings if NFC is disabled on the device.
     */
    private fun checkNfcSettings() {
        // Responsible for checking NFC settings on device.
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        // Check if NFC exists, is enabled, or disabled. If disabled, redirect to settings to
        // enable it.
        if (nfcAdapter == null) {             // Device is not NFC capable.
            Toast.makeText(this, "Device is not NFC capable.", Toast.LENGTH_LONG).show()
        } else if (!nfcAdapter.isEnabled) {   // Redirect user to settings for NFC.
            Toast.makeText(this,
                "Please enable NFC functionality in settings.", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_NFC_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {                              // NFC already enabled.
            Toast.makeText(this, "NFC is enabled.", Toast.LENGTH_SHORT).show()
        }
    }
}