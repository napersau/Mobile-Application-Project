package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.ui.adapter.DecksAdapter
import com.example.fe.viewmodel.DecksViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DecksListActivity : AppCompatActivity() {

    private lateinit var viewModel: DecksViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyTextView: TextView
    private lateinit var decksAdapter: DecksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decks_list)

        // Setup toolbar
        supportActionBar?.title = "Bộ thẻ học tập"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[DecksViewModel::class.java]

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewDecks)
        progressBar = findViewById(R.id.progressBar)
        emptyTextView = findViewById(R.id.tvEmpty)
        val fabAddDeck = findViewById<FloatingActionButton>(R.id.fabAddDeck)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        decksAdapter = DecksAdapter { deck ->
            // Navigate to DeckDetailActivity
            val intent = Intent(this, DeckDetailActivity::class.java)
            intent.putExtra("DECK_ID", deck.id)
            intent.putExtra("DECK_NAME", deck.title)
            startActivity(intent)
        }
        recyclerView.adapter = decksAdapter

        // Observe ViewModel
        viewModel.decksListResult.observe(this) { result ->
            progressBar.visibility = View.GONE
            
            result.onSuccess { decks ->
                Log.d("DecksListActivity", "Loaded ${decks.size} decks")
                if (decks.isEmpty()) {
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    decksAdapter.submitList(decks)
                }
            }
            
            result.onFailure { exception ->
                Log.e("DecksListActivity", "Failed to load decks", exception)
                Toast.makeText(
                    this,
                    "Không thể tải danh sách: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                emptyTextView.visibility = View.VISIBLE
                emptyTextView.text = "Lỗi: ${exception.message}"
            }
        }

        // FAB click listener (for future create deck feature)
        fabAddDeck.setOnClickListener {
            Toast.makeText(this, "Tính năng tạo bộ thẻ đang phát triển", Toast.LENGTH_SHORT).show()
        }

        // Load decks
        progressBar.visibility = View.VISIBLE
        viewModel.getAllDecks()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when returning
        viewModel.getAllDecks()
    }
}
