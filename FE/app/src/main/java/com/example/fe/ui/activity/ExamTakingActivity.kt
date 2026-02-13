package com.example.fe.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.*
import com.example.fe.ui.adapter.QuestionAdapter
import com.example.fe.viewmodel.ExamViewModel

class ExamTakingActivity : AppCompatActivity() {

    private lateinit var viewModel: ExamViewModel
    private var examId: Long = -1
    private var currentExam: ExamResponse? = null
    private var userAnswers = mutableMapOf<Long, String>()
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 0

    private lateinit var tvTimer: TextView
    private lateinit var tvProgress: TextView
    private lateinit var rvQuestions: RecyclerView
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var questionAdapter: QuestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_taking)

        examId = intent.getLongExtra("EXAM_ID", -1)
        if (examId == -1L) {
            Toast.makeText(this, "Không tìm thấy bài thi", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[ExamViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.getExamById(examId)
    }

    private fun initViews() {
        tvTimer = findViewById(R.id.tvTimer)
        tvProgress = findViewById(R.id.tvProgress)
        rvQuestions = findViewById(R.id.rvQuestions)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressBar = findViewById(R.id.progressBar)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Làm bài thi"
    }

    private fun setupRecyclerView() {
        questionAdapter = QuestionAdapter { questionId, answer ->
            userAnswers[questionId] = answer
            updateProgress()
        }

        rvQuestions.apply {
            layoutManager = LinearLayoutManager(this@ExamTakingActivity)
            adapter = questionAdapter
        }
    }

    private fun setupObservers() {
        viewModel.examDetailLiveData.observe(this) { result ->
            result.onSuccess { exam ->
                currentExam = exam
                startExam(exam)
            }
            result.onFailure { error ->
                Toast.makeText(
                    this,
                    "Lỗi tải bài thi: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupListeners() {
        btnSubmit.setOnClickListener {
            showSubmitConfirmation()
        }
    }

    private fun startExam(exam: ExamResponse) {
        // Start timer if duration is set
        exam.duration?.let { duration ->
            timeLeftInMillis = duration * 60 * 1000L
            startTimer()
        } ?: run {
            tvTimer.text = "Không giới hạn thời gian"
        }

        // Load all questions from all groups
        val allQuestions = mutableListOf<QuestionResponse>()
        exam.questionGroups?.forEach { group ->
            group.questions?.let { questions ->
                allQuestions.addAll(questions)
            }
        }

        questionAdapter.submitList(allQuestions)
        updateProgress()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                Toast.makeText(
                    this@ExamTakingActivity,
                    "Hết giờ! Nộp bài tự động",
                    Toast.LENGTH_SHORT
                ).show()
                submitExam()
            }
        }.start()
    }

    private fun updateTimer() {
        val hours = (timeLeftInMillis / 1000) / 3600
        val minutes = ((timeLeftInMillis / 1000) % 3600) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeText = if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }

        tvTimer.text = "⏱️ $timeText"

        // Warning when less than 5 minutes
        if (timeLeftInMillis < 5 * 60 * 1000) {
            tvTimer.setTextColor(getColor(android.R.color.holo_red_dark))
        }
    }

    private fun updateProgress() {
        currentExam?.let { exam ->
            val totalQuestions = exam.totalQuestions ?: 0
            val answeredQuestions = userAnswers.size
            tvProgress.text = "Đã làm: $answeredQuestions/$totalQuestions"
        }
    }

    private fun showSubmitConfirmation() {
        currentExam?.let { exam ->
            val totalQuestions = exam.totalQuestions ?: 0
            val answeredQuestions = userAnswers.size
            val unansweredQuestions = totalQuestions - answeredQuestions

            val message = if (unansweredQuestions > 0) {
                "Bạn còn $unansweredQuestions câu chưa trả lời.\n\nBạn có chắc muốn nộp bài?"
            } else {
                "Bạn đã hoàn thành tất cả câu hỏi.\n\nBạn có chắc muốn nộp bài?"
            }

            AlertDialog.Builder(this)
                .setTitle("Xác nhận nộp bài")
                .setMessage(message)
                .setPositiveButton("Nộp bài") { _, _ ->
                    submitExam()
                }
                .setNegativeButton("Tiếp tục làm", null)
                .show()
        }
    }

    private fun submitExam() {
        countDownTimer?.cancel()

        currentExam?.let { exam ->
            // Calculate results
            var correctAnswers = 0
            val allQuestions = mutableListOf<QuestionResponse>()

            exam.questionGroups?.forEach { group ->
                group.questions?.let { questions ->
                    allQuestions.addAll(questions)
                }
            }

            allQuestions.forEach { question ->
                val userAnswer = userAnswers[question.id]
                if (userAnswer == question.correctAnswer) {
                    correctAnswers++
                }
            }

            val totalQuestions = exam.totalQuestions ?: allQuestions.size
            val score = (correctAnswers.toFloat() / totalQuestions) * 100

            // Calculate time taken
            val timeTaken = if (exam.duration != null) {
                (exam.duration * 60 * 1000L) - timeLeftInMillis
            } else {
                0L
            }

            val result = ExamResult(
                examId = exam.id,
                examTitle = exam.title,
                totalQuestions = totalQuestions,
                correctAnswers = correctAnswers,
                userAnswers = userAnswers.map { UserAnswer(it.key, it.value) },
                timeTaken = timeTaken,
                score = score,
                completedAt = System.currentTimeMillis()
            )

            // Navigate to result screen
            val intent = Intent(this, ExamResultActivity::class.java)
            intent.putExtra("EXAM_ID", exam.id)
            intent.putExtra("CORRECT_ANSWERS", correctAnswers)
            intent.putExtra("TOTAL_QUESTIONS", totalQuestions)
            intent.putExtra("SCORE", score)
            intent.putExtra("TIME_TAKEN", timeTaken)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Thoát bài thi")
            .setMessage("Nếu thoát bây giờ, bài làm của bạn sẽ không được lưu.\n\nBạn có chắc muốn thoát?")
            .setPositiveButton("Thoát") { _, _ ->
                countDownTimer?.cancel()
                super.onBackPressed()
            }
            .setNegativeButton("Tiếp tục làm", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}

