package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.QuestionResponse

class QuestionAdapter(
    private val onAnswerSelected: (questionId: Long, answer: String) -> Unit
) : ListAdapter<QuestionResponse, QuestionAdapter.QuestionViewHolder>(QuestionDiffCallback()) {

    private val selectedAnswers = mutableMapOf<Long, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view, onAnswerSelected, selectedAnswers)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class QuestionViewHolder(
        itemView: View,
        private val onAnswerSelected: (questionId: Long, answer: String) -> Unit,
        private val selectedAnswers: MutableMap<Long, String>
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvQuestionNumber: TextView = itemView.findViewById(R.id.tvQuestionNumber)
        private val tvQuestionText: TextView = itemView.findViewById(R.id.tvQuestionText)
        private val rgAnswers: RadioGroup = itemView.findViewById(R.id.rgAnswers)
        private val rbOptionA: RadioButton = itemView.findViewById(R.id.rbOptionA)
        private val rbOptionB: RadioButton = itemView.findViewById(R.id.rbOptionB)
        private val rbOptionC: RadioButton = itemView.findViewById(R.id.rbOptionC)
        private val rbOptionD: RadioButton = itemView.findViewById(R.id.rbOptionD)

        fun bind(question: QuestionResponse) {
            tvQuestionNumber.text = "Câu ${question.questionNumber}"
            tvQuestionText.text = question.text

            rbOptionA.text = "A. ${question.optionA}"
            rbOptionB.text = "B. ${question.optionB}"
            rbOptionC.text = "C. ${question.optionC}"

            // Part 2 may not have option D
            if (question.optionD.isNullOrEmpty()) {
                rbOptionD.visibility = View.GONE
            } else {
                rbOptionD.visibility = View.VISIBLE
                rbOptionD.text = "D. ${question.optionD}"
            }

            // Clear previous selection
            rgAnswers.clearCheck()

            // Restore previous answer if exists
            selectedAnswers[question.id]?.let { answer ->
                when (answer) {
                    "A" -> rbOptionA.isChecked = true
                    "B" -> rbOptionB.isChecked = true
                    "C" -> rbOptionC.isChecked = true
                    "D" -> rbOptionD.isChecked = true
                }
            }

            // Set listener
            rgAnswers.setOnCheckedChangeListener { _, checkedId ->
                val answer = when (checkedId) {
                    R.id.rbOptionA -> "A"
                    R.id.rbOptionB -> "B"
                    R.id.rbOptionC -> "C"
                    R.id.rbOptionD -> "D"
                    else -> return@setOnCheckedChangeListener
                }
                selectedAnswers[question.id] = answer
                onAnswerSelected(question.id, answer)
            }
        }
    }

    class QuestionDiffCallback : DiffUtil.ItemCallback<QuestionResponse>() {
        override fun areItemsTheSame(
            oldItem: QuestionResponse,
            newItem: QuestionResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: QuestionResponse,
            newItem: QuestionResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}

