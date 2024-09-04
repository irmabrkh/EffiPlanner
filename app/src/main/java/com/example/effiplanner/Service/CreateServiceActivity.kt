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
class CreateServiceActivity : AppCompatActivity() {
    private lateinit var serviceName: EditText
    private lateinit var serviceDescription: EditText
    private lateinit var serviceTimeProcess: EditText

    private lateinit var createServiceButton: Button
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_service)

        serviceName = findViewById(R.id.edit_service_name)
        serviceDescription = findViewById(R.id.edit_service_description)
        serviceTimeProcess = findViewById(R.id.edit_service_time_process)
        createServiceButton = findViewById(R.id.create_service_button)
        backButton = findViewById(R.id.back_button)

        createServiceButton.setOnClickListener {
            val name = serviceName.text.toString().trim()
            val description = serviceDescription.text.toString().trim()
            val timeProcess = serviceTimeProcess.text.toString().trim()

            // check if the all data is not empty
            if (name.isEmpty()) {
                serviceName.error = "Service name is required"
                serviceName.requestFocus()
                return@setOnClickListener
            } else if (description.isEmpty()) {
                serviceDescription.error = "Service description is required"
                serviceDescription.requestFocus()
                return@setOnClickListener
            } else if (timeProcess.isEmpty()) {
                serviceTimeProcess.error = "Service time process is required"
                serviceTimeProcess.requestFocus()
                return@setOnClickListener
            }

            createService(name, description, timeProcess)
            Toast.makeText(this, "Service created successfully", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun createService(name: String, description: String, timeProcess: String) {
        val db = FirebaseDatabase.getInstance().getReference("services")
        val serviceId = db.push().key
        val service = Service(serviceId, name, description, timeProcess)

        if (serviceId != null) {
            db.child(serviceId).setValue(service).addOnCompleteListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener() {
                Toast.makeText(this, "Failed to create service: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }
}