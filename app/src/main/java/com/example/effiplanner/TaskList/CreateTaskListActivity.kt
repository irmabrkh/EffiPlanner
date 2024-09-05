package com.example.effiplanner.TaskList

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.effiplanner.Adapter.ServiceAdapter
import com.example.effiplanner.MainActivity
import com.example.effiplanner.Model.Service
import com.example.effiplanner.Model.TaskList
import com.example.effiplanner.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CreateTaskListActivity : AppCompatActivity() {

    private lateinit var taskListName: EditText
    private lateinit var serviceSpinner: Spinner

    private lateinit var serviceListView: ListView
    private lateinit var addServiceButton: Button
    private lateinit var createTaskListButton: Button
    private lateinit var backButton: Button

    private val selectedServices = mutableListOf<String>()
    private val serviceList = mutableListOf<Service>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_task_list)

        // inisialisasi view
        taskListName = findViewById(R.id.edit_task_list_name)
        serviceSpinner = findViewById(R.id.service_spinner)
        serviceListView = findViewById(R.id.service_list_view)
        addServiceButton = findViewById(R.id.add_service_button)
        createTaskListButton = findViewById(R.id.create_task_list_button)
        backButton = findViewById(R.id.back_button)

        // inisialisasi database
        database = FirebaseDatabase.getInstance().getReference("services")

        // fetch data service
        fetchService()

        // add service button
        addServiceButton.setOnClickListener {
            val selectedServiceName = serviceSpinner.selectedItem.toString()
            if (selectedServiceName.isNotEmpty() && !selectedServices.contains(selectedServiceName)) {
                selectedServices.add(selectedServiceName)
                updateServiceListView()
            }
        }

        // create task list button
        createTaskListButton.setOnClickListener {
            val name = taskListName.text.toString().trim()

            if (name.isEmpty()) {
                taskListName.error = "Task list name is required"
                taskListName.requestFocus()
                return@setOnClickListener
            }

            val taskListId = FirebaseDatabase.getInstance().getReference("task_lists").push().key

            // Buat daftar ServiceDetail berdasarkan service yang dipilih oleh pengguna
            val selectedServiceDetails = serviceList.filter { service ->
                selectedServices.contains(service.name)
            }

            val taskList = TaskList(taskListId, name, selectedServiceDetails)

            if (taskListId != null) {
                FirebaseDatabase.getInstance().getReference("task_lists")
                    .child(taskListId)
                    .setValue(taskList)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Task list created successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to create task list: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // back button
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // replace fragment TaskFragment
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun fetchService() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    serviceList.clear()
                    for (serviceSnapshot in snapshot.children) {
                        val service = serviceSnapshot.getValue(Service::class.java)
                        service?.let { serviceList.add(it) }
                    }
                    val adapter = ArrayAdapter(
                        this@CreateTaskListActivity,
                        android.R.layout.simple_spinner_item,
                        serviceList.map { it.name }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    serviceSpinner.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CreateTaskListActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun updateServiceListView() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            selectedServices
        )
        serviceListView.adapter = adapter
    }
}