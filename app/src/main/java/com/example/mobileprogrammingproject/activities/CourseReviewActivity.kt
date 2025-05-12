package com.example.mobileprogrammingproject.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileprogrammingproject.adapters.FeedbackAdapter
import com.example.mobileprogrammingproject.databinding.ActivityCourseReviewBinding
import com.example.mobileprogrammingproject.utils.ThemeUtils
import com.example.mobileprogrammingproject.viewmodels.FeedbackViewModel

class CourseReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCourseReviewBinding
    private val viewModel: FeedbackViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ThemeUtils.applyTheme(this)

        val courseId = intent.getStringExtra("COURSE_ID") ?: return
        binding.tvCourseTitle.text = "Course Reviews"

        val adapter = FeedbackAdapter()
        binding.rvFeedback.layoutManager = LinearLayoutManager(this)
        binding.rvFeedback.adapter = adapter

        viewModel.feedback.observe(this) { feedback ->
            adapter.submitList(feedback)
        }

        viewModel.fetchFeedback(courseId)
    }
}
