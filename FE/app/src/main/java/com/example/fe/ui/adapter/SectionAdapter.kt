package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.Lesson
import com.example.fe.model.Section

class SectionAdapter(
    private val sections: List<Section>,
    private val onLessonClick: (Lesson) -> Unit
) : RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {

    private val expandedSections = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount() = sections.size

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSectionTitle: TextView = itemView.findViewById(R.id.tvSectionTitle)
        private val ivExpandIcon: ImageView = itemView.findViewById(R.id.ivExpandIcon)
        private val recyclerViewLessons: RecyclerView = itemView.findViewById(R.id.recyclerViewLessons)
        private val sectionHeader: View = itemView.findViewById(R.id.sectionHeader)

        fun bind(section: Section) {
            tvSectionTitle.text = "${section.orderIndex}. ${section.title}"

            val isExpanded = expandedSections.contains(section.id)
            recyclerViewLessons.visibility = if (isExpanded) View.VISIBLE else View.GONE
            ivExpandIcon.rotation = if (isExpanded) 180f else 0f

            // Setup lessons adapter
            if (section.lessons != null && section.lessons.isNotEmpty()) {
                val lessonAdapter = LessonAdapter(section.lessons, onLessonClick)
                recyclerViewLessons.apply {
                    layoutManager = LinearLayoutManager(itemView.context)
                    adapter = lessonAdapter
                }
            }

            // Toggle expand/collapse
            sectionHeader.setOnClickListener {
                if (expandedSections.contains(section.id)) {
                    expandedSections.remove(section.id)
                } else {
                    expandedSections.add(section.id)
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }
}

