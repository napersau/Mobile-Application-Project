package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fe.R
import com.example.fe.model.Course

class CourseAdapter(
    private val onCourseClick: (Course) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view, onCourseClick)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CourseViewHolder(
        itemView: View,
        private val onCourseClick: (Course) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val ivCourseImage: ImageView = itemView.findViewById(R.id.ivCourseImage)
        private val tvCourseTitle: TextView = itemView.findViewById(R.id.tvCourseTitle)
        private val tvCourseLevel: TextView = itemView.findViewById(R.id.tvCourseLevel)
        private val tvStudentsCount: TextView = itemView.findViewById(R.id.tvStudentsCount)
        private val tvCoursePrice: TextView = itemView.findViewById(R.id.tvCoursePrice)

        fun bind(course: Course) {
            tvCourseTitle.text = course.title
            tvCourseLevel.text = course.level ?: "All Levels"
            tvStudentsCount.text = "👥 ${course.totalStudents} học viên"

            // Format price
            tvCoursePrice.text = when {
                course.price == null || course.price == 0.0 -> "Miễn phí"
                else -> "${formatPrice(course.price)} VNĐ"
            }

            // Load image with Glide
            if (!course.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(course.imageUrl)
                    .placeholder(R.drawable.ic_course)
                    .error(R.drawable.ic_course)
                    .centerCrop()
                    .into(ivCourseImage)
            } else {
                ivCourseImage.setImageResource(R.drawable.ic_course)
            }

            // Set level color
            when (course.level?.lowercase()) {
                "beginner" -> {
                    tvCourseLevel.setTextColor(itemView.context.getColor(R.color.published_green))
                }
                "intermediate" -> {
                    tvCourseLevel.setTextColor(itemView.context.getColor(R.color.draft_orange))
                }
                "advanced" -> {
                    tvCourseLevel.setTextColor(itemView.context.getColor(R.color.vocabulary_color))
                }
            }

            itemView.setOnClickListener {
                onCourseClick(course)
            }
        }

        private fun formatPrice(price: Double): String {
            return String.format("%,.0f", price)
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
}

