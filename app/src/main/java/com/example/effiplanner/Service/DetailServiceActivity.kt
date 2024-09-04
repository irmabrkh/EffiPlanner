package com.example.effiplanner.Service

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.effiplanner.R
import com.google.firebase.database.FirebaseDatabase

class DetailServiceActivity : AppCompatActivity() {
    private lateinit var serviceId: TextView
    private lateinit var serviceName: TextView
    private lateinit var serviceDescription: TextView
    private lateinit var serviceTimeProcess: TextView
    private lateinit var editService: Button
    private lateinit var deleteService: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_service)

        // Menginisialisasi view
        serviceId = findViewById(R.id.service_id)
        serviceName = findViewById(R.id.service_detail_name)
        serviceDescription = findViewById(R.id.service_detail_description)
        serviceTimeProcess = findViewById(R.id.service_detail_time_process)
        editService = findViewById(R.id.edit_service_button)
        deleteService = findViewById(R.id.delete_service_button)

        // Mendapatkan data yang dikirim dari intent
        val id = intent.getStringExtra("serviceId")
        val name = intent.getStringExtra("serviceName")
        val description = intent.getStringExtra("serviceDescription")
        val timeProcess = intent.getStringExtra("serviceTimeProcess")

        // Menampilkan data ke TextView
        serviceId.text = id
        serviceName.text = name
        serviceDescription.text = description
        serviceTimeProcess.text = timeProcess

        editService.setOnClickListener() {
            // Mengirim data ke EditServiceActivity
            val intent = Intent(this, EditServiceActivity::class.java).apply {
                putExtra("serviceId", id)
                putExtra("serviceName", name)
                putExtra("serviceDescription", description)
                putExtra("serviceTimeProcess", timeProcess)
            }
            startActivity(intent)
        }

        deleteService.setOnClickListener() {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Delete Service")
            builder.setMessage("Are you sure you want to delete this service?")
            builder.setPositiveButton("Yes") { _, _ ->
                deleteService()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }
    }

    private fun deleteService() {
        val db = FirebaseDatabase.getInstance().getReference("services")
        db.child(serviceId.text.toString()).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Service deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnSuccessListener {
                Toast.makeText(this, "Service failed to delete", Toast.LENGTH_SHORT).show()
            }
    }
}