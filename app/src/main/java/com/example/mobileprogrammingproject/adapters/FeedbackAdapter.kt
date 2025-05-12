package com.example.mobileprogrammingproject.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileprogrammingproject.databinding.ItemFeedbackBinding
import com.example.mobileprogrammingproject.models.Feedback

class FeedbackAdapter : ListAdapter<Feedback, FeedbackAdapter.FeedbackViewHolder>(FeedbackDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val binding = ItemFeedbackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeedbackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedbackViewHolder(private val binding: ItemFeedbackBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feedback: Feedback) {
            binding.tvDifficulty.text = feedback.difficulty.toString()
            binding.tvLearningExperience.text = feedback.learning_experience.toString()
            binding.tvAnonymous.text = if (feedback.anonymous) "Anonymous" else feedback.username ?: "Not Anonymous"
            binding.tvComment.text = feedback.comment ?: "No comment provided."
            binding.tvInstructorResponse.text = feedback.instructor_response ?: "No response yet."
        }
    }

    class FeedbackDiffCallback : DiffUtil.ItemCallback<Feedback>() {
        override fun areItemsTheSame(oldItem: Feedback, newItem: Feedback): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Feedback, newItem: Feedback): Boolean {
            return oldItem == newItem
        }
    }
}
