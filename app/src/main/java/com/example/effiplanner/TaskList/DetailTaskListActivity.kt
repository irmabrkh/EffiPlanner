package com.example.effiplanner.TaskList

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.effiplanner.Adapter.ServiceAdapter
import com.example.effiplanner.Model.Service
import com.example.effiplanner.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailTaskListActivity : AppCompatActivity() {

    private lateinit var taskListId: TextView
    private lateinit var taskListName: TextView
    private lateinit var editTaskList: Button
    private lateinit var deleteTaskList: Button

    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var serviceRecyclerView: RecyclerView
    private lateinit var serviceList: ArrayList<Service>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_task_list)

        // Menginisialisasi view
        taskListId = findViewById(R.id.task_list_id)
        taskListName = findViewById(R.id.task_list_detail_name)
        editTaskList = findViewById(R.id.edit_task_list_button)
        deleteTaskList = findViewById(R.id.delete_task_list_button)
        serviceRecyclerView = findViewById(R.id.task_list_detail_recycler_view)

        // Mendapatkan data yang dikirim dari intent
        val id = intent.getStringExtra("taskListId")
        val name = intent.getStringExtra("taskListName")

        // Menampilkan data ke TextView
        taskListId.text = id
        taskListName.text = name

        // Initialize service list and adapter
        serviceList = ArrayList()
        serviceAdapter = ServiceAdapter(serviceList, this)
        serviceRecyclerView.layoutManager = LinearLayoutManager(this)
        serviceRecyclerView.adapter = serviceAdapter

        // Fetch services
        fetchServices(id!!)

        // Edit task list button
        editTaskList.setOnClickListener {
            val intent = Intent(this, EditTaskListActivity::class.java)
            intent.putExtra("taskListId", id)
            intent.putExtra("taskListName", name)
            startActivity(intent)
        }

        // Delete task list button
        deleteTaskList.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Delete task list")
            builder.setMessage("Are you sure you want to delete this task list?")
            builder.setPositiveButton("Yes") { _, _ ->
                deleteTaskList(id)
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }
    }

    private fun deleteTaskList(taskListId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("task_lists").child(taskListId)
        databaseReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Task list deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete task list", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchServices(taskListId: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("task_lists").child(taskListId).child("services")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                serviceList.clear()
                for (serviceSnapshot in snapshot.children) {
                    val service = serviceSnapshot.getValue(Service::class.java)
                    service?.let { serviceList.add(it) }
                }
                serviceAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailTaskListActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}