package com.example.effiplanner.Task

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.effiplanner.Adapter.TaskListAdapter
import com.example.effiplanner.Model.TaskList
import com.example.effiplanner.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var taskId: TextView
    private lateinit var taskName: TextView
    private lateinit var taskDescription: TextView
    private lateinit var taskPriority: TextView
    private lateinit var taskStatus: TextView
    private lateinit var editTask: Button
    private lateinit var deleteTask: Button

    private lateinit var taskListAdapter: TaskListAdapter
    private lateinit var taskListRecyclerView: RecyclerView
    private lateinit var taskList: ArrayList<TaskList>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_task)

        // Menginisialisasi view
        taskId = findViewById(R.id.task_id)
        taskName = findViewById(R.id.task_detail_name)
        taskDescription = findViewById(R.id.task_detail_description)
        taskPriority = findViewById(R.id.task_detail_priority)
        taskStatus = findViewById(R.id.task_detail_status)
        editTask = findViewById(R.id.edit_task_button)
        deleteTask = findViewById(R.id.delete_task_button)
        taskListRecyclerView = findViewById(R.id.task_detail_recycler_view)

        // Mendapatkan data yang dikirim dari intent
        val id = intent.getStringExtra("taskId")
        val name = intent.getStringExtra("taskName")
        val description = intent.getStringExtra("taskDescription")
        val priority = intent.getStringExtra("taskPriority")
        val status = intent.getStringExtra("taskStatus") ?: "Open"

        // Menampilkan data ke TextView
        taskId.text = id
        taskName.text = name
        taskDescription.text = description
        taskPriority.text = priority
        taskStatus.text = status

        // Initialize task list and adapter
        taskList = ArrayList()
        taskListAdapter = TaskListAdapter(taskList, this)
        taskListRecyclerView.layoutManager = LinearLayoutManager(this)
        taskListRecyclerView.adapter = taskListAdapter

        // Fetch task lists
        fetchTaskLists(taskId = id!!)

        editTask.setOnClickListener() {
            // Mengirim data ke EditTaskActivity
            val intent = Intent(this, EditTaskActivity::class.java).apply {
                putExtra("taskId", id)
                putExtra("taskName", name)
                putExtra("taskDescription", description)
                putExtra("taskPriority", priority)
            }
            startActivity(intent)
        }

        deleteTask.setOnClickListener() {
            // buat konfirmasi sebelum menghapus task
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Delete Task")
            builder.setMessage("Are you sure you want to delete this task?")
            builder.setPositiveButton("Yes") { _, _ ->
                deleteTask()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }


    }

    // Fungsi untuk menghapus task
    private fun deleteTask() {
        val db = FirebaseDatabase.getInstance().getReference("tasks")
        db.child(taskId.text.toString()).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchTaskLists(taskId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("tasks").child(taskId).child("taskList")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for (taskListSnapshot in snapshot.children) {
                    val tasklist = taskListSnapshot.getValue(TaskList::class.java)
                    tasklist?.let {
                        taskList.add(it)
                        Log.d("DetailTaskActivity", "TaskList added: ${it.name}")
                    }
                }
                taskListAdapter.notifyDataSetChanged()
                Log.d("DetailTaskActivity", "TaskList size: ${taskList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailTaskActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}