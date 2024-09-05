package com.example.effiplanner

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.effiplanner.Auth.LoginActivity
import com.example.effiplanner.Fragment.DashboardFragment
import com.example.effiplanner.Fragment.ProfileFragment
import com.example.effiplanner.Fragment.ServiceFragment
import com.example.effiplanner.Fragment.TaskFragment
import com.example.effiplanner.Fragment.TaskListFragment
import com.example.effiplanner.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // binding: digunakan untuk mengakses view yang ada di layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // fungsi untuk mengganti fragment menggunakan bottom navigation
        binding.navbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_dashboard -> {
                    replaceFragment(DashboardFragment())
                }
                R.id.nav_task -> {
                    replaceFragment(TaskFragment())
                }
                R.id.nav_task_list -> {
                    replaceFragment(TaskListFragment())
                }
                R.id.nav_service -> {
                    replaceFragment(ServiceFragment())
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                }
            }
            true
        }

        // cek apakah sudah login atau belum
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val pindah = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(pindah)
            finish()
        } else {
            replaceFragment(DashboardFragment())
        }
    }

    // fungsi untuk mengganti fragment: digunakan untuk mengganti fragment yang sedang ditampilkan
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_navbar, fragment)
        transaction.commit()
    }
}