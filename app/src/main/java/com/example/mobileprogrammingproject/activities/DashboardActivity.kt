package com.example.mobileprogrammingproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileprogrammingproject.adapters.CourseAdapter
import com.example.mobileprogrammingproject.databinding.ActivityDashboardBinding
import com.example.mobileprogrammingproject.utils.ThemeUtils
import com.example.mobileprogrammingproject.viewmodels.DashboardViewModel

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ThemeUtils.applyTheme(this)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "student")
        val username = sharedPreferences.getString("username", null)

        // Set button visibility based on role
        if (role == "student") {
            binding.btnAddCourse.visibility = View.GONE
            binding.btnInstructorResponses.visibility = View.GONE
            binding.btnSubmitFeedback.visibility = View.VISIBLE
        } else { // instructor
            binding.btnAddCourse.visibility = View.VISIBLE
            binding.btnInstructorResponses.visibility = View.VISIBLE
            binding.btnSubmitFeedback.visibility = View.GONE
        }

        val adapter = CourseAdapter { course ->
            val intent = Intent(this, CourseReviewActivity::class.java)
            intent.putExtra("COURSE_ID", course.id)
            startActivity(intent)
        }
        binding.rvCourses.layoutManager = LinearLayoutManager(this)
        binding.rvCourses.adapter = adapter

        viewModel.courses.observe(this) { courses ->
            adapter.submitList(courses)
        }

        binding.btnSubmitFeedback.setOnClickListener {
            startActivity(Intent(this, FeedbackSubmissionActivity::class.java))
        }

        binding.btnAddCourse.setOnClickListener {
            startActivity(Intent(this, AddCourseActivity::class.java))
        }

        binding.btnInstructorResponses.setOnClickListener {
            startActivity(Intent(this, InstructorResponseActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Fetch courses, passing instructor username if role is instructor
        viewModel.fetchCourses(if (role == "instructor") username else null)
    }
}

