package com.example.mobileprogrammingproject.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileprogrammingproject.databinding.ActivityInstructorResponseBinding
import com.example.mobileprogrammingproject.models.Course
import com.example.mobileprogrammingproject.models.Feedback
import com.example.mobileprogrammingproject.network.RetrofitClient
import com.example.mobileprogrammingproject.viewmodels.FeedbackViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class InstructorResponseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInstructorResponseBinding
    private val viewModel: FeedbackViewModel by viewModels()
    private var courses: List<Course> = emptyList()
    private var feedbacks: List<Feedback> = emptyList()
    private var selectedFeedbackId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInstructorResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get logged-in instructor's username from SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val loggedInInstructor = sharedPreferences.getString("username", null)

        // Load instructors and lock to logged-in instructor
        loadInstructors(loggedInInstructor)

        // Load courses and feedbacks immediately for the logged-in instructor
        if (loggedInInstructor != null) {
            loadCourses(loggedInInstructor)
        }

        binding.spinnerInstructors.setOnItemSelectedListener { position ->
            val selectedInstructor = binding.spinnerInstructors.selectedItem?.toString()
            if (selectedInstructor != null) {
                loadCourses(selectedInstructor)
            }
        }

        binding.spinnerCourses.setOnItemSelectedListener { position ->
            val selectedCourse = binding.spinnerCourses.selectedItem?.toString()
            val selectedInstructor = binding.spinnerInstructors.selectedItem?.toString()
            if (selectedCourse != null && selectedInstructor != null) {
                loadFeedbacks(selectedInstructor, selectedCourse)
            }
        }

        binding.spinnerFeedbacks.setOnItemSelectedListener { position ->
            selectedFeedbackId = if (position >= 0 && position < feedbacks.size) feedbacks[position].id else null
        }

        binding.btnRespond.setOnClickListener {
            val responseText = binding.etResponse.text.toString()
            if (selectedFeedbackId != null && responseText.isNotBlank()) {
                MainScope().launch {
                    try {
                        val response = RetrofitClient.apiService.addInstructorResponse(
                            feedbackId = selectedFeedbackId!!,
                            response = mapOf("response" to responseText)
                        )
                        if (response.isSuccessful) {
                            Toast.makeText(this@InstructorResponseActivity, "Response submitted!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@InstructorResponseActivity, "Failed to submit response", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@InstructorResponseActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please select feedback and write a response!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadInstructors(loggedInInstructor: String?) {
        MainScope().launch {
            try {
                val response = RetrofitClient.apiService.getInstructors()
                if (response.isSuccessful) {
                    val instructors = response.body()?.map { it.username }?.distinct() ?: emptyList()
                    runOnUiThread {
                        binding.spinnerInstructors.adapter = ArrayAdapter(
                            this@InstructorResponseActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            instructors
                        )
                        // If logged-in instructor is available, select and lock them
                        if (loggedInInstructor != null && instructors.contains(loggedInInstructor)) {
                            val index = instructors.indexOf(loggedInInstructor)
                            binding.spinnerInstructors.setSelection(index)
                            binding.spinnerInstructors.isEnabled = false // Lock the spinner
                        }
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@InstructorResponseActivity, "Failed to load instructors", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadCourses(instructorName: String) {
        MainScope().launch {
            try {
                val response = RetrofitClient.apiService.getCourses(instructorName)
                if (response.isSuccessful) {
                    courses = response.body() ?: emptyList()
                    val courseNames = courses.map { it.name }
                    runOnUiThread {
                        binding.spinnerCourses.adapter = ArrayAdapter(
                            this@InstructorResponseActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            courseNames
                        )
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@InstructorResponseActivity, "Failed to load courses", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadFeedbacks(instructorName: String, courseName: String) {
        MainScope().launch {
            try {
                val response = RetrofitClient.apiService.getFeedbackByInstructor(instructorName)
                if (response.isSuccessful) {
                    feedbacks = (response.body() ?: emptyList())
                        .filter { feedback ->
                            val course = courses.find { it.id == feedback.course_id }
                            course?.name == courseName
                        }
                    val feedbackComments = feedbacks.map { f ->
                        if (f.anonymous) "Anonymous: ${f.comment ?: "(No Comment)"}"
                        else "Not Anonymous: ${f.comment ?: "(No Comment)"}"
                    }
                    runOnUiThread {
                        binding.spinnerFeedbacks.adapter = ArrayAdapter(
                            this@InstructorResponseActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            feedbackComments
                        )
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@InstructorResponseActivity, "Failed to load feedbacks", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Extension function for safe spinner selection
    private fun Spinner.setOnItemSelectedListener(onItemSelected: (position: Int) -> Unit) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                onItemSelected(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
