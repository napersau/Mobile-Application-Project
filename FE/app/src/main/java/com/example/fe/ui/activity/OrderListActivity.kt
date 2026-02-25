package com.example.fe.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.ui.adapter.OrderAdapter
import com.example.fe.viewmodel.OrderViewModel

class OrderListActivity : AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbarOrders)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Đơn hàng của tôi"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerViewOrders)
        progressBar = findViewById(R.id.progressBarOrders)
        tvEmpty = findViewById(R.id.tvEmptyOrders)

        adapter = OrderAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        viewModel.ordersLiveData.observe(this) { result ->
            progressBar.visibility = View.GONE
            result.onSuccess { orders ->
                if (orders.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    tvEmpty.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    adapter.submitList(orders)
                }
            }.onFailure { error ->
                Toast.makeText(this, error.message ?: "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show()
                tvEmpty.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.getAllOrders()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

