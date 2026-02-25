package com.example.fe.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.OrderResponse
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class OrderAdapter : ListAdapter<OrderResponse, OrderAdapter.OrderViewHolder>(DiffCallback()) {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        .withZone(ZoneId.systemDefault())

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCourseTitle: TextView = itemView.findViewById(R.id.tvOrderCourseTitle)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvOrderAmount)
        private val tvTxnRef: TextView = itemView.findViewById(R.id.tvOrderTxnRef)
        private val tvDate: TextView = itemView.findViewById(R.id.tvOrderDate)

        fun bind(order: OrderResponse) {
            tvCourseTitle.text = order.courseTitle ?: itemView.context.getString(R.string.feature_grammar)
            tvTxnRef.text = itemView.context.getString(R.string.order_txn_ref, order.vnpTxnRef ?: "-")
            tvAmount.text = order.amount?.let { "${formatPrice(it)} VNĐ" } ?: "-"
            tvDate.text = order.createdDate?.let { dateFormatter.format(it) } ?: "-"

            when (order.status) {
                "SUCCESS" -> {
                    tvStatus.setText(R.string.order_status_success)
                    tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                }
                "PENDING" -> {
                    tvStatus.setText(R.string.order_status_pending)
                    tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_orange_dark))
                }
                "FAILED" -> {
                    tvStatus.setText(R.string.order_status_failed)
                    tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
                }
                else -> {
                    tvStatus.text = order.status ?: "-"
                }
            }
        }

        private fun formatPrice(price: Double): String =
            String.format(Locale.getDefault(), "%,.0f", price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderResponse>() {
        override fun areItemsTheSame(oldItem: OrderResponse, newItem: OrderResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: OrderResponse, newItem: OrderResponse) =
            oldItem == newItem
    }
}
