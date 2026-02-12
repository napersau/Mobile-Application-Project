package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.DocumentResponse
import java.text.SimpleDateFormat
import java.util.*

class DocumentAdapter(
    private val onItemClick: (DocumentResponse) -> Unit,
    private val onEditClick: (DocumentResponse) -> Unit,
    private val onDeleteClick: (DocumentResponse) -> Unit,
    private var isAdmin: Boolean = false
) : ListAdapter<DocumentResponse, DocumentAdapter.DocumentViewHolder>(DocumentDiffCallback()) {

    fun updateAdminStatus(isAdmin: Boolean) {
        this.isAdmin = isAdmin
        notifyDataSetChanged() // Update tất cả items để hiển thị/ẩn nút admin
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = getItem(position)
        holder.bind(document, isAdmin)
    }

    inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.textTitle)
        private val descriptionText: TextView = itemView.findViewById(R.id.textDescription)
        private val categoryText: TextView = itemView.findViewById(R.id.textCategory)
        private val viewCountText: TextView = itemView.findViewById(R.id.textViewCount)
        private val dateText: TextView = itemView.findViewById(R.id.textDate)
        @Suppress("unused")
        private val thumbnailImage: ImageView = itemView.findViewById(R.id.imageThumbnail)
        private val publishedIndicator: TextView = itemView.findViewById(R.id.textPublished)
        
        // Admin buttons
        private val editButton: Button = itemView.findViewById(R.id.buttonEdit)
        private val deleteButton: Button = itemView.findViewById(R.id.buttonDelete)
        private val adminButtonsContainer: View = itemView.findViewById(R.id.adminButtonsContainer)

        fun bind(document: DocumentResponse, isAdmin: Boolean) {
            titleText.text = document.title
            descriptionText.text = document.description.ifEmpty { "Không có mô tả" }
            categoryText.text = getCategoryDisplayName(document.category.name)
            viewCountText.text = itemView.context.getString(
                R.string.view_count_format,
                document.viewCount
            )

            // Format date - Convert Instant to Date without API 26 methods
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val millis = document.createdAt.epochSecond * 1000 + document.createdAt.nano / 1000000
            val date = Date(millis)
            dateText.text = dateFormat.format(date)

            // Published status
            publishedIndicator.visibility = if (document.isPublished) View.VISIBLE else View.GONE
            publishedIndicator.text = if (document.isPublished) "Đã xuất bản" else "Nháp"
            
            // Admin controls
            adminButtonsContainer.visibility = if (isAdmin) View.VISIBLE else View.GONE
            
            // Click listeners
            itemView.setOnClickListener { 
                onItemClick(document) 
            }
            
            editButton.setOnClickListener { 
                onEditClick(document) 
            }
            
            deleteButton.setOnClickListener { 
                onDeleteClick(document) 
            }
            
            // Load thumbnail (if using image loading library like Glide)
            // Glide.with(itemView.context)
            //     .load(document.thumbnail)
            //     .placeholder(R.drawable.placeholder_document)
            //     .error(R.drawable.placeholder_document)
            //     .into(thumbnailImage)
        }
        
        private fun getCategoryDisplayName(category: String): String {
            return when (category) {
                "GRAMMAR" -> "Ngữ pháp"
                "VOCABULARY" -> "Từ vựng"
                "SKILLS" -> "Kỹ năng"
                "EXAM_PREPARATION" -> "Luyện thi"
                else -> category
            }
        }
    }

    class DocumentDiffCallback : DiffUtil.ItemCallback<DocumentResponse>() {
        override fun areItemsTheSame(oldItem: DocumentResponse, newItem: DocumentResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DocumentResponse, newItem: DocumentResponse): Boolean {
            return oldItem == newItem
        }
    }
}