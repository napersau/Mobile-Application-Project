package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.Lesson
import com.example.fe.model.LessonType

class LessonAdapter(
    private val lessons: List<Lesson>,
    private val onLessonClick: (Lesson) -> Unit
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(lessons[position])
    }

    override fun getItemCount() = lessons.size

    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvLessonIcon: TextView = itemView.findViewById(R.id.tvLessonIcon)
        private val tvLessonTitle: TextView = itemView.findViewById(R.id.tvLessonTitle)
        private val tvLessonType: TextView = itemView.findViewById(R.id.tvLessonType)
        private val tvLessonDuration: TextView = itemView.findViewById(R.id.tvLessonDuration)
        private val tvFreeBadge: TextView = itemView.findViewById(R.id.tvFreeBadge)

        fun bind(lesson: Lesson) {
            tvLessonTitle.text = "${lesson.orderIndex}. ${lesson.title}"

            // Set icon and type based on lesson type
            when (lesson.type) {
                LessonType.VIDEO -> {
                    tvLessonIcon.text = "▶"
                    tvLessonType.text = "Video"
                }
                LessonType.DOCUMENT -> {
                    tvLessonIcon.text = "📄"
                    tvLessonType.text = "Tài liệu"
                }
                LessonType.FLASHCARD -> {
                    tvLessonIcon.text = "📚"
                    tvLessonType.text = "Flashcard"
                }
                LessonType.EXAM -> {
                    tvLessonIcon.text = "📝"
                    tvLessonType.text = "Bài kiểm tra"
                }
            }

            // Set duration
            if (lesson.duration != null && lesson.duration > 0) {
                val minutes = lesson.duration / 60
                tvLessonDuration.visibility = View.VISIBLE
                tvLessonDuration.text = "• $minutes phút"
            } else {
                tvLessonDuration.visibility = View.GONE
            }

            // Show free badge
            tvFreeBadge.visibility = if (lesson.isFree) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onLessonClick(lesson)
            }
        }
    }
}

