package com.example.mobileprogrammingproject.activities
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mobileprogrammingproject.databinding.ActivityFeedbackSubmissionBinding
import com.example.mobileprogrammingproject.models.Course
import com.example.mobileprogrammingproject.models.FeedbackRequest
import com.example.mobileprogrammingproject.network.RetrofitClient
import com.example.mobileprogrammingproject.utils.ThemeUtils
import com.example.mobileprogrammingproject.utils.ValidationUtils
import com.example.mobileprogrammingproject.viewmodels.DashboardViewModel
import com.example.mobileprogrammingproject.viewmodels.FeedbackViewModel
import kotlinx.coroutines.launch

class FeedbackSubmissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackSubmissionBinding
    private val viewModel: FeedbackViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var selectedCourse: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackSubmissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ThemeUtils.applyTheme(this)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        val username = sharedPreferences.getString("username", null)

        dashboardViewModel.fetchCourses()
        dashboardViewModel.courses.observe(this) { courses ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, courses.map { "${it.name} - ${it.instructor}" })
            binding.spinnerCourses.adapter = adapter
            binding.spinnerCourses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedCourse = courses[position]
                    updateSubmitButtonState()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedCourse = null
                    updateSubmitButtonState()
                }
            }
        }

        binding.btnSubmit.isEnabled = false
        binding.rbDifficulty.setOnRatingBarChangeListener { _, _, _ -> updateSubmitButtonState() }
        binding.rbLearningExperience.setOnRatingBarChangeListener { _, _, _ -> updateSubmitButtonState() }

        binding.btnSubmit.setOnClickListener {
            val difficulty = binding.rbDifficulty.rating.toInt()
            val learningExperience = binding.rbLearningExperience.rating.toInt()
            val comment = binding.etComment.text.toString()
            val anonymous = binding.switchAnonymous.isChecked
            if (userId != null && selectedCourse != null) {
                viewModel.submitFeedback(
                    courseId = selectedCourse!!.id,
                    userId = userId,
                    difficulty = difficulty,
                    learningExperience = learningExperience,
                    comment = comment,
                    anonymous = anonymous,
                    username = if (!anonymous) username else null
                ).observe(this) { response ->
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    if (response.success) {
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Please select a course and complete ratings!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSubmitButtonState() {
        val difficulty = binding.rbDifficulty.rating.toInt()
        val learningExperience = binding.rbLearningExperience.rating.toInt()
        binding.btnSubmit.isEnabled = ValidationUtils.isValidFeedback(difficulty, learningExperience) && selectedCourse != null
    }

    private fun FeedbackViewModel.submitFeedback(
        courseId: String,
        userId: String?,
        difficulty: Int,
        learningExperience: Int,
        comment: String?,
        anonymous: Boolean,
        username: String?
    ): LiveData<SubmissionResponse> {
        val responseLiveData = MutableLiveData<SubmissionResponse>()
        viewModelScope.launch {
            try {
                val feedbackRequest = FeedbackRequest(
                    course_id = courseId,
                    difficulty = difficulty,
                    learning_experience = learningExperience,
                    comment = comment,
                    anonymous = anonymous,
                    user_id = if (!anonymous) userId else null,
                    username = username
                )
                val response = RetrofitClient.apiService.submitFeedback(feedbackRequest)
                responseLiveData.postValue(
                    if (response.isSuccessful) {
                        SubmissionResponse(true, response.body()?.get("message") ?: "Feedback submitted")
                    } else {
                        SubmissionResponse(false, response.errorBody()?.string() ?: "Submission failed")
                    }
                )
            } catch (e: Exception) {
                responseLiveData.postValue(SubmissionResponse(false, "Network error"))
            }
        }
        return responseLiveData
    }
}

data class SubmissionResponse(val success: Boolean, val message: String)
