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

class EditTaskListActivity : AppCompatActivity() {

    private lateinit var taskListName: EditText

    private lateinit var serviceSpinner: Spinner
    private lateinit var serviceListView: ListView
    private lateinit var addServiceButton: Button
    private lateinit var updateTaskListButton: Button
    private lateinit var backButton: Button

    private val selectedServices = mutableListOf<String>()
    private val serviceList = mutableListOf<Service>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_task_list)

        // Ambil data dari intent
        val taskListId = intent.getStringExtra("taskListId")!!
        val name = intent.getStringExtra("taskListName")!!

        // Inisialisasi views
        taskListName = findViewById(R.id.edit_task_list_name)
        serviceSpinner = findViewById(R.id.service_spinner)
        serviceListView = findViewById(R.id.service_list_view)
        addServiceButton = findViewById(R.id.add_service_button)
        updateTaskListButton = findViewById(R.id.update_task_list_button)
        backButton = findViewById(R.id.back_button)

        // Menampilkan data ke TextView
        taskListName.setText(name)

        // Inisialisasi database
        database = FirebaseDatabase.getInstance().getReference("services")

        // Fetch data service
        fetchService()

        // Load existing services
        loadExistingServices(taskListId)

        // Add service button
        addServiceButton.setOnClickListener {
            val selectedService = serviceSpinner.selectedItem.toString()
            if (selectedService.isNotEmpty() && !selectedServices.contains(selectedService)) {
                selectedServices.add(selectedService)
                updateServiceListView()
                Toast.makeText(this, "Service added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Service already added", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup ListView item click listener for removal
        serviceListView.setOnItemClickListener { _, _, position, _ ->
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Remove service")
            builder.setMessage("Are you sure you want to remove this service?")
            builder.setPositiveButton("Yes") { _, _ ->
                selectedServices.removeAt(position)
                updateServiceListView()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }

        // Update task list button
        updateTaskListButton.setOnClickListener {
            val name = taskListName.text.toString().trim()

            if (name.isEmpty()) {
                taskListName.error = "Task list name is required"
                taskListName.requestFocus()
                return@setOnClickListener
            }

            updateTaskList(taskListId, name)
        }

        // Back button
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchService() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    serviceList.clear()
                    for (data in snapshot.children) {
                        val service = data.getValue(Service::class.java)
                        service?.let { serviceList.add(it) }
                    }
                    val adapter = ArrayAdapter(
                        this@EditTaskListActivity,
                        android.R.layout.simple_spinner_item,
                        serviceList.map { it.name }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    serviceSpinner.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditTaskListActivity, error.message, Toast.LENGTH_SHORT).show()
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

    private fun loadExistingServices(taskListId: String) {
        val taskListRef = FirebaseDatabase.getInstance().getReference("task_lists").child(taskListId)
        taskListRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = snapshot.getValue(TaskList::class.java)
                taskList?.services?.forEach { service ->
                    service.name?.let { selectedServices.add(it) }
                }
                updateServiceListView()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditTaskListActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTaskList(taskListId: String, name: String) {
        val db = FirebaseDatabase.getInstance().getReference("task_lists").child(taskListId)
        val selectedService = serviceList.filter { serviceList ->
            selectedServices.contains(serviceList.name)
        }

        val updatedTaskList = TaskList(taskListId, name, selectedService)

        db.setValue(updatedTaskList).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Task list updated successfully", Toast.LENGTH_SHORT).show()
                // kembali ke main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update task list: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}