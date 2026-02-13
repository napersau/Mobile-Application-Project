package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fe.R
import com.example.fe.model.ExamResponse
import com.example.fe.model.ExamType
import com.example.fe.viewmodel.ExamViewModel

class ExamDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: ExamViewModel
    private var examId: Long = -1
    private var currentExam: ExamResponse? = null

    private lateinit var tvTitle: TextView
    private lateinit var tvType: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvTotalQuestions: TextView
    private lateinit var tvParts: TextView
    private lateinit var btnStartExam: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var contentLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_detail)

        examId = intent.getLongExtra("EXAM_ID", -1)
        if (examId == -1L) {
            Toast.makeText(this, "Không tìm thấy bài thi", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[ExamViewModel::class.java]

        initViews()
        setupObservers()
        setupListeners()

        viewModel.getExamById(examId)
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvExamTitle)
        tvType = findViewById(R.id.tvExamType)
        tvDescription = findViewById(R.id.tvExamDescription)
        tvDuration = findViewById(R.id.tvExamDuration)
        tvTotalQuestions = findViewById(R.id.tvExamTotalQuestions)
        tvParts = findViewById(R.id.tvExamParts)
        btnStartExam = findViewById(R.id.btnStartExam)
        progressBar = findViewById(R.id.progressBar)
        contentLayout = findViewById(R.id.contentLayout)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chi tiết bài thi"
    }

    private fun setupObservers() {
        viewModel.examDetailLiveData.observe(this) { result ->
            result.onSuccess { exam ->
                currentExam = exam
                displayExamDetails(exam)
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
            contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun setupListeners() {
        btnStartExam.setOnClickListener {
            currentExam?.let { exam ->
                val intent = Intent(this, ExamTakingActivity::class.java)
                intent.putExtra("EXAM_ID", exam.id)
                startActivity(intent)
            }
        }
    }

    private fun displayExamDetails(exam: ExamResponse) {
        tvTitle.text = exam.title
        tvType.text = getExamTypeLabel(exam.type)
        tvDescription.text = exam.description ?: "Không có mô tả"

        tvDuration.text = if (exam.duration != null) {
            "${exam.duration} phút"
        } else {
            "Không giới hạn"
        }

        tvTotalQuestions.text = "${exam.totalQuestions ?: 0} câu hỏi"

        // Display parts information
        val partsInfo = StringBuilder()
        exam.questionGroups?.let { groups ->
            partsInfo.append("Bài thi bao gồm ${groups.size} phần:\n\n")
            groups.forEachIndexed { index, group ->
                val questionCount = group.questions?.size ?: 0
                partsInfo.append("${index + 1}. ${getPartTypeLabel(group.type)}\n")
                partsInfo.append("   • $questionCount câu hỏi\n")
                if (!group.audioUrl.isNullOrEmpty()) {
                    partsInfo.append("   • Có file nghe\n")
                }
                if (!group.imageUrl.isNullOrEmpty()) {
                    partsInfo.append("   • Có hình ảnh\n")
                }
                partsInfo.append("\n")
            }
        } ?: run {
            partsInfo.append("Thông tin chi tiết chưa có sẵn")
        }

        tvParts.text = partsInfo.toString()
    }

    private fun getExamTypeLabel(type: ExamType): String {
        return when (type) {
            ExamType.TOEIC_FULL_TEST -> "TOEIC Full Test (200 câu)"
            ExamType.TOEIC_MINI_TEST -> "TOEIC Mini Test"
            ExamType.IELTS_ACADEMIC -> "IELTS Academic"
            ExamType.IELTS_GENERAL -> "IELTS General"
            ExamType.MOCK_TEST -> "Mock Test"
        }
    }

    private fun getPartTypeLabel(type: com.example.fe.model.PartType): String {
        return when (type) {
            com.example.fe.model.PartType.PART_1 -> "Part 1 - Photographs"
            com.example.fe.model.PartType.PART_2 -> "Part 2 - Question-Response"
            com.example.fe.model.PartType.PART_3 -> "Part 3 - Conversations"
            com.example.fe.model.PartType.PART_4 -> "Part 4 - Short Talks"
            com.example.fe.model.PartType.PART_5 -> "Part 5 - Incomplete Sentences"
            com.example.fe.model.PartType.PART_6 -> "Part 6 - Text Completion"
            com.example.fe.model.PartType.PART_7 -> "Part 7 - Reading Comprehension"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

