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
import com.example.effiplanner.Adapter.TaskListAdapter
import com.example.effiplanner.Model.TaskList
import com.example.effiplanner.R
import com.example.effiplanner.Task.CreateTaskActivity
import com.example.effiplanner.TaskList.CreateTaskListActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class TaskListFragment : Fragment() {
    private lateinit var createTaskListButton: Button

    private lateinit var taskListRecyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter
    private lateinit var taskList: ArrayList<TaskList>
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inisialisasi taskList dan DatabaseReference
        taskList = ArrayList()
        db = FirebaseDatabase.getInstance().getReference("task_lists")

        // ambil data dari firebase
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for (taskListSnapshot in snapshot.children) {
                    val tasklist = taskListSnapshot.getValue(TaskList::class.java)
                    taskList.add(tasklist!!)
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
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inisialisasi recyclerview dan adapter
        taskListRecyclerView = view.findViewById(R.id.task_list_recycler_view)
        taskListRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TaskListAdapter(taskList, requireContext())
        taskListRecyclerView.adapter = adapter

        createTaskListButton = view.findViewById(R.id.add_task_list_button)
        createTaskListButton.setOnClickListener {
            val intent = Intent(activity, CreateTaskListActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}