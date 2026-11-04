package com.example.simblocker

import android.Manifest
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val switchBlocker = findViewById<Switch>(R.id.switchBlocker)
        val btnPermissions = findViewById<Button>(R.id.btnPermissions)

        btnPermissions.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE),
                101
            )
            Toast.makeText(this, "Permisos solicitados. Asegúrate de dar acceso.", Toast.LENGTH_LONG).show()
        }

        switchBlocker.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Filtro de SIM activado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Filtro desactivado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
