package com.example.effiplanner.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.effiplanner.Model.Task
import com.example.effiplanner.R
import com.example.effiplanner.Task.DetailTaskActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TaskAdapter(
    private val taskList: ArrayList<Task>,
    private val context: Context,
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ambil data dari list task
        val task = taskList[position]
        holder.taskName.text = task.name
        holder.taskDescription.text = task.description
        holder.taskPriority.text = task.priority
        holder.taskStatus.text = task.status

        holder.itemView.setOnClickListener() {
            val intent = Intent(context, DetailTaskActivity::class.java).apply {
                putExtra("taskId", task.id)
                putExtra("taskName", task.name)
                putExtra("taskDescription", task.description)
                putExtra("taskPriority", task.priority)
                putExtra("taskStatus", task.status)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.task_name)
        val taskDescription: TextView = itemView.findViewById(R.id.task_description)
        val taskPriority: TextView = itemView.findViewById(R.id.task_priority)
        val taskStatus: TextView = itemView.findViewById(R.id.task_status)
    }
}