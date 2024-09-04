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

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // auth: digunakan untuk mengakses fitur autentikasi dari Firebase
        auth = FirebaseAuth.getInstance()

        // deklarasi view
        val emailEdit = findViewById<EditText>(R.id.edit_email)
        val passwordEdit = findViewById<EditText>(R.id.edit_password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)

        // set listener pada login button
        loginButton.setOnClickListener {
            // try-catch digunakan untuk menangkap error yang terjadi
            try {
                // jika tidak terjadi error, maka akan menjalankan kode di dalam blok try
                val email = emailEdit.text.toString()
                val password = passwordEdit.text.toString()
                loginUser(email, password)
            } catch (e: Exception) {
                // jika terjadi error, maka akan menjalankan kode di dalam blok catch
                Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // set listener pada button
        registerButton.setOnClickListener() {
            // pindah ke halaman register menggunakan intent
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    // fungsi untuk login user
    private fun loginUser(email: String, password: String) {
        // signInWithEmailAndPassword: digunakan untuk login user menggunakan email dan password dari Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // cek apakah user sudah login dan email sudah diverifikasi jika iya maka pindah ke halaman MainActivity jika tidak maka tampilkan pesan
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}