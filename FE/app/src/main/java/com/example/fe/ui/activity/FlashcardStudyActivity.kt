package com.example.fe.ui.activity

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fe.R
import com.example.fe.model.FlashcardResponse
import com.example.fe.utils.StudyTimeTracker
import com.example.fe.viewmodel.DecksViewModel
import com.google.android.material.card.MaterialCardView

class FlashcardStudyActivity : AppCompatActivity() {

    private lateinit var viewModel: DecksViewModel
    private lateinit var cardView: MaterialCardView
    private lateinit var tvQuestion: TextView
    private lateinit var tvAnswer: TextView
    private lateinit var tvProgress: TextView
    private lateinit var btnFlip: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var progressBar: ProgressBar

    private var flashcards: List<FlashcardResponse> = emptyList()
    private var currentIndex = 0
    private var isShowingAnswer = false
    private val studyTimeTracker = StudyTimeTracker("FlashcardStudy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard_study)

        val deckId = intent.getLongExtra("DECK_ID", -1L)
        val deckName = intent.getStringExtra("DECK_NAME") ?: "Học thẻ"

        // Setup toolbar
        supportActionBar?.title = deckName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this)[DecksViewModel::class.java]

        // Initialize views
        cardView = findViewById(R.id.cardViewFlashcard)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvAnswer = findViewById(R.id.tvAnswer)
        tvProgress = findViewById(R.id.tvProgress)
        btnFlip = findViewById(R.id.btnFlip)
        btnNext = findViewById(R.id.btnNext)
        btnPrevious = findViewById(R.id.btnPrevious)
        progressBar = findViewById(R.id.progressBar)

        // Setup click listeners
        cardView.setOnClickListener {
            flipCard()
        }

        btnFlip.setOnClickListener {
            flipCard()
        }

        btnNext.setOnClickListener {
            showNextCard()
        }

        btnPrevious.setOnClickListener {
            showPreviousCard()
        }

        // Observe ViewModel
        viewModel.deckDetailResult.observe(this) { result ->
            progressBar.visibility = View.GONE

            result.onSuccess { deck ->
                flashcards = deck.flashcardsList ?: emptyList()

                if (flashcards.isEmpty()) {
                    Toast.makeText(this, "Bộ thẻ này chưa có flashcard nào", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showCurrentCard()
                }
            }

            result.onFailure { exception ->
                Toast.makeText(
                    this,
                    "Không thể tải flashcards: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
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

    private fun showCurrentCard() {
        if (flashcards.isEmpty()) return

        val card = flashcards[currentIndex]

        // Reset to question side
        isShowingAnswer = false
        tvQuestion.visibility = View.VISIBLE
        tvAnswer.visibility = View.GONE

        // Set content - use term as question, definition as answer
        tvQuestion.text = card.term
        tvAnswer.text = card.definition

        // Update progress
        tvProgress.text = "${currentIndex + 1} / ${flashcards.size}"

        // Update button states
        btnPrevious.isEnabled = currentIndex > 0
        btnNext.isEnabled = currentIndex < flashcards.size - 1

        // Change button text on last card
        if (currentIndex == flashcards.size - 1) {
            btnNext.text = getString(R.string.complete)
        } else {
            btnNext.text = getString(R.string.next)
        }
    }

    private fun flipCard() {
        // Create flip animation - use simple fade animation
        val flipAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        cardView.startAnimation(flipAnimation)

        // Toggle between question and answer
        if (isShowingAnswer) {
            tvAnswer.visibility = View.GONE
            tvQuestion.visibility = View.VISIBLE
            btnFlip.text = getString(R.string.show_answer)
        } else {
            tvQuestion.visibility = View.GONE
            tvAnswer.visibility = View.VISIBLE
            btnFlip.text = getString(R.string.show_question)
        }

        isShowingAnswer = !isShowingAnswer
    }

    private fun showNextCard() {
        if (currentIndex < flashcards.size - 1) {
            currentIndex++
            showCurrentCard()
        } else {
            // Finished studying
            Toast.makeText(this, "Bạn đã học xong bộ thẻ này! 🎉", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun showPreviousCard() {
        if (currentIndex > 0) {
            currentIndex--
            showCurrentCard()
        }
    }

    override fun onResume() {
        super.onResume()
        studyTimeTracker.start()
    }

    override fun onPause() {
        super.onPause()
        studyTimeTracker.stop()
    }

    override fun onSupportNavigateUp(): Boolean {
        showExitConfirmation()
        return true
    }

    private fun showExitConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Thoát học tập?")
            .setMessage("Bạn có chắc muốn dừng học không?")
            .setPositiveButton("Thoát") { _, _ ->
                finish()
            }
            .setNegativeButton("Tiếp tục học", null)
            .show()
    }
}
