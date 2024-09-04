package com.example.effiplanner.Auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.effiplanner.MainActivity
import com.example.effiplanner.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val nameEdit = findViewById<EditText>(R.id.edit_name)
        val emailEdit = findViewById<EditText>(R.id.edit_email)
        val passwordEdit = findViewById<EditText>(R.id.edit_password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)

        registerButton.setOnClickListener {
            try {
                val name = nameEdit.text.toString()
                val email = emailEdit.text.toString()
                val password = passwordEdit.text.toString()
                registerUser(name, email, password)
            } catch (e: Exception) {
                Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener() {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                    // Perbarui profil pengguna dengan nama pengguna
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener() { profileTask ->
                            if (profileTask.isSuccessful) {
                                // Save user role
                                saveUserRole(user.uid, name, email, "user")

                                // Send verification email
                                user.sendEmailVerification()
                                    .addOnCompleteListener { verificationTask ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                                        } else {
                                            Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Failed to update user profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserRole(uid: String, name: String, email: String, role: String) {
        val db = FirebaseDatabase.getInstance().getReference("users")
        val userMap = mapOf<String, Any>(
            "name" to name,
            "email" to email,
            "role" to role
        )
        db.child(uid).setValue(userMap).addOnSuccessListener {
            Toast.makeText(this, "User role saved", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to save user role", Toast.LENGTH_SHORT).show()
        }
    }
}