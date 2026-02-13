package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.ExamResponse
import com.example.fe.model.ExamType

class ExamAdapter(
    private val onExamClick: (ExamResponse) -> Unit
) : ListAdapter<ExamResponse, ExamAdapter.ExamViewHolder>(ExamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exam, parent, false)
        return ExamViewHolder(view, onExamClick)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ExamViewHolder(
        itemView: View,
        private val onExamClick: (ExamResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvExamTitle: TextView = itemView.findViewById(R.id.tvExamTitle)
        private val tvExamType: TextView = itemView.findViewById(R.id.tvExamType)
        private val tvExamDuration: TextView = itemView.findViewById(R.id.tvExamDuration)
        private val tvExamQuestions: TextView = itemView.findViewById(R.id.tvExamQuestions)
        private val tvExamDescription: TextView = itemView.findViewById(R.id.tvExamDescription)

        fun bind(exam: ExamResponse) {
            tvExamTitle.text = exam.title
            tvExamType.text = getExamTypeLabel(exam.type)

            tvExamDuration.text = if (exam.duration != null) {
                "⏱️ ${exam.duration} phút"
            } else {
                "⏱️ Không giới hạn"
            }

            tvExamQuestions.text = "📝 ${exam.totalQuestions ?: 0} câu hỏi"

            tvExamDescription.text = exam.description ?: "Không có mô tả"
            tvExamDescription.visibility = if (exam.description.isNullOrEmpty()) View.GONE else View.VISIBLE

            itemView.setOnClickListener {
                onExamClick(exam)
            }
        }

        private fun getExamTypeLabel(type: ExamType): String {
            return when (type) {
                ExamType.TOEIC_FULL_TEST -> "📄 TOEIC Full Test"
                ExamType.TOEIC_MINI_TEST -> "📋 TOEIC Mini Test"
                ExamType.IELTS_ACADEMIC -> "🎓 IELTS Academic"
                ExamType.IELTS_GENERAL -> "📚 IELTS General"
                ExamType.MOCK_TEST -> "✏️ Mock Test"
            }
        }
    }

    class ExamDiffCallback : DiffUtil.ItemCallback<ExamResponse>() {
        override fun areItemsTheSame(oldItem: ExamResponse, newItem: ExamResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExamResponse, newItem: ExamResponse): Boolean {
            return oldItem == newItem
        }
    }
}

