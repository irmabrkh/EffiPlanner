package com.example.effiplanner.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.effiplanner.Adapter.TaskAdapter
import com.example.effiplanner.Task.CreateTaskActivity
import com.example.effiplanner.Model.Task
import com.example.effiplanner.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TaskFragment : Fragment() {
    private lateinit var createTaskButton: Button

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var taskList: ArrayList<Task>
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inisialisasi taskList dan DatabaseReference
        taskList = ArrayList()
        db = FirebaseDatabase.getInstance().getReference("tasks")

        // ambil data dari firebase
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    taskList.add(task!!)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inisialisasi ArrayList dan Adapter
        taskRecyclerView = view.findViewById(R.id.task_recycler_view)
        taskRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TaskAdapter(taskList, requireContext())
        taskRecyclerView.adapter = adapter

        createTaskButton = view.findViewById(R.id.add_task_button)
        createTaskButton.setOnClickListener {
            val intent = Intent(activity, CreateTaskActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}