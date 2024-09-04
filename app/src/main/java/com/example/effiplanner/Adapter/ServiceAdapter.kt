package com.example.effiplanner.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.effiplanner.R
import com.example.effiplanner.Service.DetailServiceActivity

class ServiceAdapter(
    private val serviceList: ArrayList<Service>,
    private val context: Context,
): RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.service_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ambil data dari list service
        val service = serviceList[position]
        holder.serviceName.text = service.name
        holder.serviceDescription.text = service.description
        holder.serviceTimeProcess.text = service.timeProcess

        holder.itemView.setOnClickListener() {
            val intent = Intent(context, DetailServiceActivity::class.java).apply {
                putExtra("serviceId", service.id)
                putExtra("serviceName", service.name)
                putExtra("serviceDescription", service.description)
                putExtra("serviceTimeProcess", service.timeProcess)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val serviceName: TextView = itemView.findViewById(R.id.service_name)
        val serviceDescription: TextView = itemView.findViewById(R.id.service_description)
        val serviceTimeProcess: TextView = itemView.findViewById(R.id.service_time_process)
    }

}