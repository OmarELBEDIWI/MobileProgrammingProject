package com.example.mobileprogrammingproject.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileprogrammingproject.databinding.ActivityLoginBinding
import com.example.mobileprogrammingproject.utils.ValidationUtils
import com.example.mobileprogrammingproject.viewmodels.DashboardViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            if (ValidationUtils.isValidCredentials(username, password)) {
                Log.d("LoginDebug", "Attempting login for $username")
                viewModel.login(username, password).observe(this) { result ->
                    Log.d("LoginDebug", "Login Result: $result")
                    if (result.success && result.userId != null) {
                        Log.d("LoginDebug", "Login successful, navigating to Dashboard")
                        with(sharedPreferences.edit()) {
                            putString("user_id", result.userId)
                            putString("role", result.role)
                            putString("username", result.username)
                            apply()
                        }
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                        Log.d("LoginDebug", "Intent to DashboardActivity started")
                    } else {
                        Log.d("LoginDebug", "Login failed: ${result.message}")
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.d("LoginDebug", "Invalid credentials entered")
                Toast.makeText(this, "Please enter valid credentials.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterUserActivity::class.java)
            startActivity(intent)
        }
    }
}
