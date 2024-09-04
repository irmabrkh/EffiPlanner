package com.example.effiplanner.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class DashboardFragment : Fragment() {
    // Deklarasi tipedata FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Deklarasi tipedata view
    private lateinit var welcomeText: TextView
    private lateinit var verifyEmailText: TextView
    private lateinit var roleText: TextView

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
        return inflater.inflate(R.layout.fragment_dashoard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Deklarasi view
        welcomeText = view.findViewById(R.id.welcome_text)
        verifyEmailText = view.findViewById(R.id.verification_text)
        roleText = view.findViewById(R.id.role_text)

        // Set skeleton loading from koleton
        welcomeText.loadSkeleton(length = 20)
        verifyEmailText.loadSkeleton(length = 54)
        roleText.loadSkeleton(length = 20)

        // ambil data dari firebase
        getData()
    }

    // get data from firebase
    private fun getData() {
        val user = auth.currentUser
        if (user != null) {
            val db = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val isVerified = user.isEmailVerified

                    // sembunyikan skeleton loading
                    welcomeText.hideSkeleton()
                    verifyEmailText.hideSkeleton()
                    roleText.hideSkeleton()

                    // pass data ke view
                    welcomeText.text = "Welcome, $name!"
                    if (isVerified) {
                        verifyEmailText.text = "Your email ($email) has been verified."
                        roleText.text = "Your role is ${snapshot.child("role").value}."
                    } else {
                        verifyEmailText.text = "Please verify your email ($email)."
                        roleText.text = "Your role is ${snapshot.child("role").value}."
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Toast.makeText(context, "Failed to read value: $error", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // No user is signed in
            Intent(activity, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
}
