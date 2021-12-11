package com.example.settingsfinal

import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent

class AppSelector : AppCompatActivity() {
    private lateinit var packageList: ListView
    private var arrayAdapter: ArrayAdapter<*>? = null

    // Name, Package Name, Icon
    private lateinit var selectedPackage: Triple<String, String, Drawable>
    // Name, Package Name, Icon
    private val appInfo: MutableList<Triple<String, String, Drawable>> = mutableListOf()
    private val activityId = "AppSelector"
    private lateinit var packageText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_selector)
        supportActionBar?.hide();

        packageList = findViewById(R.id.listView)
        packageText = findViewById<View>(R.id.package_identifier) as EditText

        // Button: Goes to activity to write the tag.
        val writeButton = findViewById<Button>(R.id.writeButton)
        writeButton.setOnClickListener {
            val intent = Intent(this, WriteNFC::class.java)
            intent.putExtra("id", activityId) //Where the write data came from.
            intent.putExtra("contents", packageText.text.toString()) // What will be written.
            startActivity(intent)
        }
    }

    /**
     * Sets up the spinner view for the apps list and associated listeners.
     */
    override fun onStart() {
        super.onStart()

        buildPackageDisplay()           // Obtain the needed descriptions about each app.
        selectPackageSpinner()          // Build a spinner to select the package from.
        packageListener()               // Allow tracking of user selections.
    }

    /**
     * Assemble the package name, its icon, and its identifier into a list.
     * Removes system services from the list.
     */
    private fun buildPackageDisplay() {
        val installedPackages = packageManager.getInstalledPackages(0)

        // Assemble needed info (name, package, icon) about each app.
        for (item in installedPackages.indices) {
            // Filter out system services that do not have a launch intent.
            if (packageManager.getLaunchIntentForPackage(installedPackages[item]
                    .packageName) != null) {
                val packageInfo: PackageInfo = installedPackages[item]
                val packageName = packageInfo.packageName
                val name = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                val icon: Drawable =
                    packageManager.getApplicationIcon(installedPackages[item].packageName)

                appInfo.add(Triple(name, packageName, icon))
            }
        }
    }

    /**
     * Create a spinner to show package details.
     */
    private fun selectPackageSpinner() {
        for (item in appInfo) {
            arrayAdapter = ArrayAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, appInfo as List<*>)
            packageList.adapter = arrayAdapter
            }
        }

    /**
     * Selects a package when the users taps on it.
     */
    private fun packageListener() {
        packageList.setOnItemClickListener { parent, _, position, _ ->
            selectedPackage = parent.getItemAtPosition(position) as Triple<String, String, Drawable>
            // Since the user can input into the search bar that input should be used when sending
            // the package name to be written.
            packageText.setText(selectedPackage.second)
            Toast.makeText(this, packageText.text, Toast.LENGTH_SHORT).show()
        }
    }
}