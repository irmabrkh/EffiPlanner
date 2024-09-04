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
import com.example.effiplanner.Adapter.ServiceAdapter
import com.example.effiplanner.R
import com.example.effiplanner.Service.CreateServiceActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ServiceFragment : Fragment() {
    private lateinit var createServiceButton: Button

    private lateinit var serviceRecyclerView: RecyclerView
    private lateinit var adapter: ServiceAdapter
    private lateinit var serviceList: ArrayList<Service>
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inisialisasi serviceList dan DatabaseReference
        serviceList = ArrayList()
        db = FirebaseDatabase.getInstance().getReference("services")

        // ambil data dari firebase
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                serviceList.clear()
                for (serviceSnapshot in snapshot.children) {
                    val service = serviceSnapshot.getValue(Service::class.java)
                    serviceList.add(service!!)
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
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // inisialisasi ArrayList dan Adapter
        serviceRecyclerView = view.findViewById(R.id.service_recycler_view)
        serviceRecyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = ServiceAdapter(serviceList, requireContext())
        serviceRecyclerView.adapter = adapter

        createServiceButton = view.findViewById(R.id.add_service_button)
        createServiceButton.setOnClickListener {
            val intent = Intent(activity, CreateServiceActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}