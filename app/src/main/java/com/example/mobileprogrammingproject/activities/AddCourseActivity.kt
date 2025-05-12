package com.example.mobileprogrammingproject.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileprogrammingproject.databinding.ActivityAddCourseBinding
import com.example.mobileprogrammingproject.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCourseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCourseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get logged-in instructor's username from SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val loggedInInstructor = sharedPreferences.getString("username", null)

        // Load instructors into spinner and lock to logged-in instructor
        loadInstructors(loggedInInstructor)

        binding.btnAddCourse.setOnClickListener {
            val courseName = binding.etCourseName.text.toString()
            val instructorName = binding.spinnerInstructor.selectedItem?.toString()
            if (courseName.isNotEmpty() && !instructorName.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitClient.apiService.addCourse(
                            mapOf("name" to courseName, "instructor" to instructorName)
                        )
                        runOnUiThread {
                            if (response.isSuccessful) {
                                Toast.makeText(this@AddCourseActivity, "Course added successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@AddCourseActivity, "Failed to add course", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@AddCourseActivity, "Error occurred", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please enter course name and select an instructor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadInstructors(loggedInInstructor: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getInstructors()
                if (response.isSuccessful) {
                    val instructors = response.body()?.map { it.username } ?: emptyList()
                    runOnUiThread {
                        val adapter = ArrayAdapter(this@AddCourseActivity, android.R.layout.simple_spinner_dropdown_item, instructors)
                        binding.spinnerInstructor.adapter = adapter
                        // If logged-in instructor is available, select and lock them
                        if (loggedInInstructor != null && instructors.contains(loggedInInstructor)) {
                            val index = instructors.indexOf(loggedInInstructor)
                            binding.spinnerInstructor.setSelection(index)
                            binding.spinnerInstructor.isEnabled = false // Lock the spinner
                        }
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@AddCourseActivity, "Failed to load instructors", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
