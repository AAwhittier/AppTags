package com.example.settingsfinal

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.Ndef
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import java.io.IOException

class WriteNFC : AppCompatActivity() {
    private object WriteNFCConstants{
        const val NO_TAG: String = "No tag detected: Please bring a tag near the NFC beacon of " +
                "the phone. The beacon is commonly located in the center of the back of the phone."
        const val WRITE_SUCCESS = "Tag written successfully."
    }
    private lateinit var pendingIntent: PendingIntent
    private var tagDispatch: Array<IntentFilter> = arrayOf()

    private lateinit var id: String
    private lateinit var contents: String

    private lateinit var record: NdefRecord
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var target: Tag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_nfc)
        supportActionBar?.hide()

        // Early end if the device is not capable of NFC.
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Not available until the context is created.
        val writeData: Bundle? = intent.extras
        readExtras(writeData)
        // Activates tag to write the message.
        val writeButton = findViewById<Button>(R.id.write)
        writeButton.setOnClickListener {
            tagStatus()
        }
    }

    /**
     * Shows a pop up alert when an attempt to write a tag is made without a tag being near the
     * device.
     */
    private fun alertNoTag() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(WriteNFCConstants.NO_TAG)
            .setPositiveButton("Confirm", DialogInterface.OnClickListener {
                dialog, _ -> dialog.cancel()
        })

        val alert = dialogBuilder.create()
        alert.show()
    }

    /**
     * If a tag is near, write to it.
     * If a tag is not detected show an error.
     */
    private fun tagStatus() {
        try {
            //TODO target is never null
            if (target == null) {
            } else {
                writeNFCMessage(target)
                Toast.makeText(this, WriteNFCConstants.WRITE_SUCCESS, Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            alertNoTag()
        } catch (e: FormatException) {
            alertNoTag()
        }
    }

    /**
     * Handle disabling foreground dispatch.
     */
    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    /**
     * Handle enabling foreground dispatch.
     */
    override fun onResume() {
        super.onResume()
        prepareForDispatch()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, tagDispatch, null)
    }

    /**
     * Prepare the pending intent and tag detection for when a tag is brought near the device.
     */
    private fun prepareForDispatch() {
        pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, WriteNFC::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT)
        tagDispatch = arrayOf(tagDetected)
    }

    /**
     * Handle a new intent when a tag is recognized, this initializes the tag.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            target = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
        }
    }

    /**
     * Create an NFC record based on the type of activity it came from.
     */
    private fun createNFCRecord(id: String, message: String): NdefRecord {
        // Create records based on the type requested.
        when (id) {
            "AppSelector" -> {
                record = NdefRecord.createApplicationRecord(message)
            }
            "NFCText" -> {
                record = NdefRecord.createTextRecord("EN", message)
            }
            "NFCUrl" -> {
                record = NdefRecord.createUri(message)
            }
        }
        return record
    }

    /**
     * Writes a record to an NFC tag.
     */
    private fun writeNFCMessage(tag: Tag) {
        var message = NdefMessage(createNFCRecord(id, contents))
        var ndef = Ndef.get(tag)
        ndef.connect()
        ndef.writeNdefMessage(message)
        ndef.close()
    }

    /**
     * Null check intent extras, store the intent extra data.
     */
    private fun readExtras(writeData: Bundle?) {
        if (writeData != null) {
            id = writeData.getString("id").toString() // Extract data from intent extras.
            contents = writeData.getString("contents").toString()
        }
    }
}