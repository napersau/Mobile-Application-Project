package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.DecksResponse

class DecksAdapter(
    private val onDeckClick: (DecksResponse) -> Unit
) : ListAdapter<DecksResponse, DecksAdapter.DeckViewHolder>(DeckDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_deck, parent, false)
        return DeckViewHolder(view, onDeckClick)
    }

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DeckViewHolder(
        itemView: View,
        private val onDeckClick: (DecksResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvDeckName: TextView = itemView.findViewById(R.id.tvDeckName)
        private val tvDeckDescription: TextView = itemView.findViewById(R.id.tvDeckDescription)
        private val tvCardCount: TextView = itemView.findViewById(R.id.tvCardCount)

        fun bind(deck: DecksResponse) {
            tvDeckName.text = deck.title
            tvDeckDescription.text = deck.description ?: "Không có mô tả"
            tvCardCount.text = "${deck.totalFlashcards ?: 0} thẻ"

            itemView.setOnClickListener {
                onDeckClick(deck)
            }
        }
    }

    class DeckDiffCallback : DiffUtil.ItemCallback<DecksResponse>() {
        override fun areItemsTheSame(oldItem: DecksResponse, newItem: DecksResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DecksResponse, newItem: DecksResponse): Boolean {
            return oldItem == newItem
        }
    }
}
