package com.example.fe.ui.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val messageContainer: LinearLayout = itemView.findViewById(R.id.messageContainer)
        private val messageText: TextView = itemView.findViewById(R.id.tvMessage)
        private val timestampText: TextView = itemView.findViewById(R.id.tvTimestamp)
        private val messageCard: View = itemView.findViewById(R.id.messageCard)

        fun bind(message: ChatMessage) {
            messageText.text = message.message

            // Format timestamp
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            timestampText.text = sdf.format(Date(message.timestamp))

            // Configure layout based on sender
            val layoutParams = messageContainer.layoutParams as ViewGroup.MarginLayoutParams

            if (message.isUser) {
                // User message - right aligned, blue background
                messageContainer.gravity = Gravity.END
                layoutParams.leftMargin = dpToPx(48)
                layoutParams.rightMargin = dpToPx(8)
                messageCard.setBackgroundResource(R.drawable.bg_chat_user)
                messageText.setTextColor(itemView.context.getColor(android.R.color.white))
                timestampText.setTextColor(itemView.context.getColor(android.R.color.white))
            } else {
                // AI message - left aligned, gray background
                messageContainer.gravity = Gravity.START
                layoutParams.leftMargin = dpToPx(8)
                layoutParams.rightMargin = dpToPx(48)
                messageCard.setBackgroundResource(R.drawable.bg_chat_ai)
                messageText.setTextColor(itemView.context.getColor(R.color.text_primary))
                timestampText.setTextColor(itemView.context.getColor(R.color.text_secondary))
            }

            messageContainer.layoutParams = layoutParams
        }

        private fun dpToPx(dp: Int): Int {
            return (dp * itemView.context.resources.displayMetrics.density).toInt()
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}

