package com.example.fe.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fe.R

class ExamResultActivity : AppCompatActivity() {

    private lateinit var tvScore: TextView
    private lateinit var tvCorrectAnswers: TextView
    private lateinit var tvTotalQuestions: TextView
    private lateinit var tvTimeTaken: TextView
    private lateinit var tvResultMessage: TextView
    private lateinit var tvListeningScore: TextView
    private lateinit var tvReadingScore: TextView
    private lateinit var btnReviewAnswers: Button
    private lateinit var btnBackToList: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_result)

        initViews()
        displayResults()
        setupListeners()
    }

    private fun initViews() {
        tvScore = findViewById(R.id.tvScore)
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers)
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions)
        tvTimeTaken = findViewById(R.id.tvTimeTaken)
        tvResultMessage = findViewById(R.id.tvResultMessage)
        tvListeningScore = findViewById(R.id.tvListeningScore)
        tvReadingScore = findViewById(R.id.tvReadingScore)
        btnReviewAnswers = findViewById(R.id.btnReviewAnswers)
        btnBackToList = findViewById(R.id.btnBackToList)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kết quả bài thi"
    }

    private fun displayResults() {
        val examResultId = intent.getLongExtra("EXAM_RESULT_ID", -1)
        val examId = intent.getLongExtra("EXAM_ID", -1)
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)
        val score = intent.getFloatExtra("SCORE", 0f)
        val timeTaken = intent.getLongExtra("TIME_TAKEN", 0)
        val listeningScore = intent.getIntExtra("LISTENING_SCORE", 0)
        val readingScore = intent.getIntExtra("READING_SCORE", 0)

        // Display score
        tvScore.text = String.format("%.1f%%", score)

        // Display correct answers
        tvCorrectAnswers.text = "$correctAnswers"
        tvTotalQuestions.text = "$totalQuestions"

        // Display listening and reading scores (for TOEIC)
        if (listeningScore > 0 || readingScore > 0) {
            tvListeningScore.text = "$listeningScore"
            tvReadingScore.text = "$readingScore"
            tvListeningScore.visibility = View.VISIBLE
            tvReadingScore.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvListeningLabel)?.visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvReadingLabel)?.visibility = View.VISIBLE
        } else {
            tvListeningScore.visibility = View.GONE
            tvReadingScore.visibility = View.GONE
            findViewById<TextView>(R.id.tvListeningLabel)?.visibility = View.GONE
            findViewById<TextView>(R.id.tvReadingLabel)?.visibility = View.GONE
        }

        // Display time taken
        val hours = (timeTaken / 1000) / 3600
        val minutes = ((timeTaken / 1000) % 3600) / 60
        val seconds = (timeTaken / 1000) % 60

        val timeText = if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
        tvTimeTaken.text = timeText

        // Display result message
        val message = when {
            score >= 90 -> "🎉 Xuất sắc! Kết quả tuyệt vời!"
            score >= 80 -> "👏 Rất tốt! Bạn đã làm rất tốt!"
            score >= 70 -> "😊 Tốt! Tiếp tục cố gắng!"
            score >= 60 -> "📚 Khá! Hãy luyện tập thêm!"
            else -> "💪 Cần cố gắng hơn! Đừng bỏ cuộc!"
        }
        tvResultMessage.text = message
    }

    private fun setupListeners() {
        btnReviewAnswers.setOnClickListener {
            // TODO: Navigate to review answers screen
            // Show detailed review of all questions with correct/incorrect answers
            // and explanations
        }

        btnBackToList.setOnClickListener {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}

