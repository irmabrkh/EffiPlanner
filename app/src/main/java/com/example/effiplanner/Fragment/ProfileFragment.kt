package com.example.effiplanner.Fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.effiplanner.Auth.LoginActivity
import com.example.effiplanner.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import koleton.api.hideSkeleton
import koleton.api.loadSkeleton

class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    private lateinit var displayName: TextView
    private lateinit var emailText: TextView
    private lateinit var logoutButton: Button

    private lateinit var profileLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayName = view.findViewById(R.id.welcome_text)
        emailText = view.findViewById(R.id.email_text)
        logoutButton = view.findViewById(R.id.logout_button)
        profileLayout = view.findViewById(R.id.profile_layout)

        profileLayout.loadSkeleton()

        getData()

        logoutButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Logout")
            builder.setMessage("Are you sure want to logout?")
            builder.setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                Toast.makeText(context, "Logout success", Toast.LENGTH_SHORT).show()
                // buat delay selama 1 detik sebelum pindah ke halaman login
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.show()
        }
    }

    private fun getData() {
        val user = auth.currentUser
        if (user != null) {
            val db = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()

                    profileLayout.hideSkeleton()

                    // Tampilkan data ke TextView
                    displayName.text = name
                    emailText.text = email

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to read value", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
        }
    }
}