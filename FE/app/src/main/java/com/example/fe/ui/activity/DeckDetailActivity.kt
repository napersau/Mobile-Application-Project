package com.example.fe.ui.activity

import android.annotation.SuppressLint
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
import com.example.fe.ui.adapter.FlashcardsAdapter
import com.example.fe.viewmodel.DecksViewModel

class DeckDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: DecksViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyTextView: TextView
    private lateinit var tvDeckName: TextView
    private lateinit var tvDeckDescription: TextView
    private lateinit var tvTotalCards: TextView
    private lateinit var flashcardsAdapter: FlashcardsAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deck_detail)

        val deckId = intent.getLongExtra("DECK_ID", -1L)
        val deckName = intent.getStringExtra("DECK_NAME") ?: "Chi tiết bộ thẻ"

        // Setup toolbar
        supportActionBar?.title = deckName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[DecksViewModel::class.java]

        // Initialize views
        tvDeckName = findViewById(R.id.tvDeckName)
        tvDeckDescription = findViewById(R.id.tvDeckDescription)
        tvTotalCards = findViewById(R.id.tvTotalCards)
        recyclerView = findViewById(R.id.recyclerViewFlashcards)
        progressBar = findViewById(R.id.progressBar)
        emptyTextView = findViewById(R.id.tvEmpty)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        flashcardsAdapter = FlashcardsAdapter()
        recyclerView.adapter = flashcardsAdapter

        // Observe ViewModel
        viewModel.deckDetailResult.observe(this) { result ->
            progressBar.visibility = View.GONE
            
            result.onSuccess { deck ->
                Log.d("DeckDetailActivity", "Loaded deck: ${deck.title} with ${deck.flashcardsList?.size ?: 0} cards")
                
                tvDeckName.text = deck.title
                tvDeckDescription.text = deck.description ?: "Không có mô tả"
                tvTotalCards.text = "Tổng số thẻ: ${deck.flashcardsList?.size ?: 0}"
                
                if (deck.flashcardsList.isNullOrEmpty()) {
                    emptyTextView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyTextView.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    flashcardsAdapter.submitList(deck.flashcardsList)
                }
            }
            
            result.onFailure { exception ->
                Log.e("DeckDetailActivity", "Failed to load deck", exception)
                Toast.makeText(
                    this,
                    "Không thể tải chi tiết: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                emptyTextView.visibility = View.VISIBLE
                emptyTextView.text = "Lỗi: ${exception.message}"
            }
        }

        // Load deck detail
        if (deckId != -1L) {
            progressBar.visibility = View.VISIBLE
            viewModel.getDeckById(deckId)
        } else {
            Toast.makeText(this, "ID bộ thẻ không hợp lệ", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
