package com.example.mobileprogrammingproject.activities
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileprogrammingproject.databinding.ActivityRegisterUserBinding
import com.example.mobileprogrammingproject.utils.ThemeUtils
import com.example.mobileprogrammingproject.utils.ValidationUtils
import com.example.mobileprogrammingproject.viewmodels.DashboardViewModel

class RegisterUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterUserBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ThemeUtils.applyTheme(this)

        // Set up role spinner
        val roles = arrayOf("student", "instructor")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinnerRole.adapter = adapter

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            val role = binding.spinnerRole.selectedItem.toString()
            if (ValidationUtils.isValidCredentials(username, password)) {
                viewModel.register(username, password, role).observe(this) { response ->
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    if (response.success) {
                        // After registration, go back to login screen
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Username must not be empty and password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

