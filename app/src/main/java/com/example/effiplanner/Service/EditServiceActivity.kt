package com.example.effiplanner.Service

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.effiplanner.MainActivity
import com.example.effiplanner.Model.Service
import com.example.effiplanner.R
import com.google.firebase.database.FirebaseDatabase

class EditServiceActivity : AppCompatActivity() {
    private lateinit var serviceName: EditText
    private lateinit var serviceDescription: EditText
    private lateinit var serviceTimeProcess: EditText

    private lateinit var updateServiceButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_service)

        // Ambil data dari intent
        val serviceId = intent.getStringExtra("serviceId")!!
        val name = intent.getStringExtra("serviceName")!!
        val description = intent.getStringExtra("serviceDescription")!!
        val timeProcess = intent.getStringExtra("serviceTimeProcess")!!

        // Inisialisasi views
        serviceName = findViewById(R.id.edit_service_name)
        serviceDescription = findViewById(R.id.edit_service_description)
        serviceTimeProcess = findViewById(R.id.edit_service_time_process)
        updateServiceButton = findViewById(R.id.update_service_button)
        backButton = findViewById(R.id.back_button)

        // Set nilai awal pada fields
        serviceName.setText(name)
        serviceDescription.setText(description)
        serviceTimeProcess.setText(timeProcess)

        // Event handler untuk tombol Update
        updateServiceButton.setOnClickListener {
            val updatedName = serviceName.text.toString().trim()
            val updatedDescription = serviceDescription.text.toString().trim()
            val updatedTimeProcess = serviceTimeProcess.text.toString().trim()

            // check if the all data is not empty
            if (updatedName.isEmpty()) {
                serviceName.error = "Service name is required"
                serviceName.requestFocus()
                return@setOnClickListener
            } else if (updatedDescription.isEmpty()) {
                serviceDescription.error = "Service description is required"
                serviceDescription.requestFocus()
                return@setOnClickListener
            } else if (updatedTimeProcess.isEmpty()) {
                serviceTimeProcess.error = "Service time process is required"
                serviceTimeProcess.requestFocus()
                return@setOnClickListener
            }

            // Update service
            updateService(serviceId, updatedName, updatedDescription, updatedTimeProcess)
        }

        // Event handler untuk tombol Kembali
        backButton.setOnClickListener {
            finish()
        }

    }

    private fun updateService(
        serviceId: String,
        updatedName: String,
        updatedDescription: String,
        updatedTimeProcess: String
    ) {
        val db = FirebaseDatabase.getInstance().getReference("services")
        val updatedService = Service(serviceId, updatedName, updatedDescription, updatedTimeProcess)

        // Simpan perubahan ke Firebase
        db.child(serviceId).setValue(updatedService).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Service updated successfully", Toast.LENGTH_SHORT).show()
                // kembali ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update service: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update service: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}