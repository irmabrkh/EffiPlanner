package com.example.effiplanner.Task

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
import com.example.effiplanner.MainActivity
import com.example.effiplanner.Model.Task
import com.example.effiplanner.Model.TaskList
import com.example.effiplanner.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditTaskActivity : AppCompatActivity() {

    private lateinit var taskName: EditText
    private lateinit var taskDescription: EditText
    private lateinit var taskPriority: Spinner
    private lateinit var taskStatus: Spinner

    private lateinit var taskListSpinner: Spinner
    private lateinit var taskListView: ListView
    private lateinit var addTaskListButton: Button
    private lateinit var updateTaskButton: Button
    private lateinit var backButton: Button

    private val selectedTaskList = mutableListOf<String>()
    private val taskList = mutableListOf<TaskList>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_task)

        // Ambil data dari intent
        val taskId = intent.getStringExtra("taskId")!!
        val name = intent.getStringExtra("taskName")!!
        val description = intent.getStringExtra("taskDescription")!!
        val priority = intent.getStringExtra("taskPriority")!!
        val status = intent.getStringExtra("taskStatus") ?: "Open"

        // Inisialisasi views
        taskName = findViewById(R.id.edit_task_name)
        taskDescription = findViewById(R.id.edit_task_description)
        taskPriority = findViewById(R.id.priority_spinner)
        taskStatus = findViewById(R.id.status_spinner)
        taskListSpinner = findViewById(R.id.task_list_spinner)
        taskListView = findViewById(R.id.task_list_view)
        addTaskListButton = findViewById(R.id.add_task_list_button)
        updateTaskButton = findViewById(R.id.update_task_button)
        backButton = findViewById(R.id.back_button)

        // Setup spinner untuk task priority
        val prioritySpinner = resources.getStringArray(R.array.priority_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, prioritySpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskPriority.adapter = adapter
        taskPriority.setSelection(adapter.getPosition(priority))

        // Setup spinner untuk task status
        val statusSpinner = resources.getStringArray(R.array.status_array)
        val adapterStatus = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusSpinner)
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskStatus.adapter = adapterStatus
        taskStatus.setSelection(adapterStatus.getPosition(status))

        // Set nilai awal pada fields
        taskName.setText(name)
        taskDescription.setText(description)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("task_lists")

        // fetch data task list
        fetchTaskLists()

        // Add task list button
        addTaskListButton.setOnClickListener {
            val selectedTaskListName = taskListSpinner.selectedItem.toString()
            if (selectedTaskListName.isNotEmpty() && !selectedTaskList.contains(selectedTaskListName)) {
                selectedTaskList.add(selectedTaskListName)
                updateTaskListView()
                Toast.makeText(this, "Task list added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task list already added", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup ListView item click listener for removal
        taskListView.setOnItemClickListener { _, _, position, _ ->
            // buat konfirmasi sebelum menghapus task list
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Remove Task List")
            builder.setMessage("Are you sure you want to remove this task list?")
            builder.setPositiveButton("Yes") { _, _ ->
                val taskListToRemove = selectedTaskList[position]
                selectedTaskList.remove(taskListToRemove)
                updateTaskListView()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }

        // Load existing task lists associated with the task
        loadExistingTaskLists(taskId)

        // Event handler untuk tombol Update
        updateTaskButton.setOnClickListener {
            val updatedName = taskName.text.toString().trim()
            val updatedDescription = taskDescription.text.toString().trim()
            val updatedPriority = taskPriority.selectedItem.toString().trim()
            val updatedStatus = taskStatus.selectedItem.toString().trim()

            // Validasi input
            if (updatedName.isEmpty()) {
                taskName.error = "Task name is required"
                taskName.requestFocus()
                return@setOnClickListener
            }
            if (updatedDescription.isEmpty()) {
                taskDescription.error = "Task description is required"
                taskDescription.requestFocus()
                return@setOnClickListener
            }

            // Update task
            updateTask(taskId, updatedName, updatedDescription, updatedPriority, updatedStatus)
        }

        // Event handler untuk tombol Back
        backButton.setOnClickListener {
            finish()
        }
    }

    // Fungsi untuk mengambil data task list dari Firebase Realtime Database
    private fun fetchTaskLists() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    taskList.clear()
                    for (serviceSnapshot in snapshot.children) {
                        val tasklist = serviceSnapshot.getValue(TaskList::class.java)
                        tasklist?.let { taskList.add(it) }
                    }
                    val adapter = ArrayAdapter(
                        this@EditTaskActivity,
                        android.R.layout.simple_spinner_item,
                        taskList.map { it.name }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    taskListSpinner.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditTaskActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi untuk menampilkan task list yang sudah dipilih
    private fun updateTaskListView() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            selectedTaskList
        )
        taskListView.adapter = adapter
    }

    // Fungsi untuk mengambil task list yang sudah ada
    private fun loadExistingTaskLists(taskId: String) {
        val taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId)
        taskRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val task = snapshot.getValue(Task::class.java)
                task?.taskList?.forEach { taskListItem ->
                    taskListItem.name?.let { selectedTaskList.add(it) }
                }
                updateTaskListView()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditTaskActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi untuk mengupdate task
    private fun updateTask(taskId: String, name: String, description: String, priority: String, status: String) {
        val db = FirebaseDatabase.getInstance().getReference("tasks")
        val selectedTaskLists = taskList.filter { taskList ->
            selectedTaskList.contains(taskList.name)
        }

        val updatedTask = Task(taskId, name, description, priority, status, selectedTaskLists)

        // Simpan perubahan ke Firebase Realtime Database
        db.child(taskId).setValue(updatedTask).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                // kembali ke main activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update task: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}