package com.example.simblocker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var switchSim1: Switch
    private lateinit var switchSim2: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val switchBlocker = findViewById<Switch>(R.id.switchBlocker)
        switchSim1 = findViewById<Switch>(R.id.switchSim1)
        switchSim2 = findViewById<Switch>(R.id.switchSim2)
        val btnPermissions = findViewById<Button>(R.id.btnPermissions)

        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        switchBlocker.isChecked = prefs.getBoolean("blocking_active", true)
        switchSim1.isChecked = prefs.getBoolean("block_sim_1", true)
        switchSim2.isChecked = prefs.getBoolean("block_sim_2", true)

        switchBlocker.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("blocking_active", isChecked).apply()
        }
        switchSim1.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("block_sim_1", isChecked).apply()
        }
        switchSim2.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("block_sim_2", isChecked).apply()
        }

        btnPermissions.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG
                ),
                101
            )
            Toast.makeText(this, "Solicitando permisos...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                val subscriptionManager = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                val subInfoList = subscriptionManager?.activeSubscriptionInfoList
                if (!subInfoList.isNullOrEmpty()) {
                    if (subInfoList.size > 0 && subInfoList[0]?.displayName != null) {
                        switchSim1.text = "Bloquear: ${subInfoList[0].displayName}"
                    }
                    if (subInfoList.size > 1 && subInfoList[1]?.displayName != null) {
                        switchSim2.text = "Bloquear: ${subInfoList[1].displayName}"
                    }
                }
            }
        } catch (e: Exception) {
            // Protección contra errores en eSIMs
        }
    }
}
