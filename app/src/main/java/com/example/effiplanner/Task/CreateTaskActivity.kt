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
import com.example.effiplanner.Model.Service
import com.example.effiplanner.Model.Task
import com.example.effiplanner.Model.TaskList
import com.example.effiplanner.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class CreateTaskActivity : AppCompatActivity() {
    private lateinit var taskName: EditText
    private lateinit var taskDescription: EditText
    private lateinit var taskPriority: Spinner

    private lateinit var taskListSpinner: Spinner
    private lateinit var taskListView: ListView
    private lateinit var addTaskListButton: Button
    private lateinit var createTaskButton: Button
    private lateinit var backButton: Button

    private val selectedTaskList = mutableListOf<String>()
    private val taskList = mutableListOf<TaskList>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_task)

        val PrioritySpinner = resources.getStringArray(R.array.priority_array)

        // Initialize views
        taskName = findViewById(R.id.edit_task_name)
        taskDescription = findViewById(R.id.edit_task_description)
        taskPriority = findViewById(R.id.priority_spinner)
        taskListSpinner = findViewById(R.id.task_list_spinner)
        taskListView = findViewById(R.id.task_list_view)
        addTaskListButton = findViewById(R.id.add_task_list_button)
        createTaskButton = findViewById(R.id.create_task_button)
        backButton = findViewById(R.id.back_button)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("task_lists")

        // fetch data task list
        fetchTaskLists()

        // add task list button
        addTaskListButton.setOnClickListener {
            val selectedTaskListName = taskListSpinner.selectedItem.toString()
            if (selectedTaskListName.isNotEmpty() && !selectedTaskList.contains(selectedTaskListName)) {
                selectedTaskList.add(selectedTaskListName)
                updateTaskListView()
            }
        }

        if (taskPriority != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, PrioritySpinner)
            taskPriority.adapter = adapter
        }

        createTaskButton.setOnClickListener {
            val name = taskName.text.toString().trim()
            val description = taskDescription.text.toString().trim()
            val priority = taskPriority.selectedItem.toString().trim()

            // check if the all data is not empty
            if (name.isEmpty()) {
                taskName.error = "Task name is required"
                taskName.requestFocus()
                return@setOnClickListener
            } else if (description.isEmpty()) {
                taskDescription.error = "Task description is required"
                taskDescription.requestFocus()
                return@setOnClickListener
            } else if (priority.isEmpty()) {
                Toast.makeText(this, "Task priority is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val taskId = FirebaseDatabase.getInstance().getReference("tasks").push().key

            // buat daftar TaskList berdasarkan task list yang dipilih oleh pengguna
            val selectedTaskLists = taskList.filter { taskList ->
                selectedTaskList.contains(taskList.name)
            }

            val task = Task(taskId, name, description, priority, status = "Open", taskList = selectedTaskLists)

            if (taskId != null) {
                FirebaseDatabase.getInstance().getReference("tasks")
                    .child(taskId)
                    .setValue(task)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to create task: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // replace fragment TaskFragment
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

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
                        this@CreateTaskActivity,
                        android.R.layout.simple_spinner_item,
                        taskList.map { it.name }
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    taskListSpinner.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CreateTaskActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateTaskListView() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            selectedTaskList
        )
        taskListView.adapter = adapter
    }
}