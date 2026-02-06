package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.FlashcardResponse

class FlashcardsAdapter : ListAdapter<FlashcardResponse, FlashcardsAdapter.FlashcardViewHolder>(FlashcardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flashcard, parent, false)
        return FlashcardViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FlashcardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        private val tvAnswer: TextView = itemView.findViewById(R.id.tvAnswer)
        private val tvDifficulty: TextView = itemView.findViewById(R.id.tvDifficulty)

        fun bind(flashcard: FlashcardResponse) {
            tvQuestion.text = "📝 ${flashcard.term}"
            tvAnswer.text = "💡 ${flashcard.definition}"
            
            flashcard.flashcardsProgress?.difficulty?.let {
                tvDifficulty.text = "📊 $it"
                tvDifficulty.visibility = View.VISIBLE
            } ?: run {
                tvDifficulty.visibility = View.GONE
            }
        }
    }

    class FlashcardDiffCallback : DiffUtil.ItemCallback<FlashcardResponse>() {
        override fun areItemsTheSame(oldItem: FlashcardResponse, newItem: FlashcardResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FlashcardResponse, newItem: FlashcardResponse): Boolean {
            return oldItem == newItem
        }
    }
}
