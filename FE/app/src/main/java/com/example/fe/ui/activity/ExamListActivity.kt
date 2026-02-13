package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.ExamType
import com.example.fe.ui.adapter.ExamAdapter
import com.example.fe.viewmodel.ExamViewModel

class ExamListActivity : AppCompatActivity() {

    private lateinit var viewModel: ExamViewModel
    private lateinit var examAdapter: ExamAdapter
    private lateinit var rvExams: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: View
    private lateinit var tvTitle: TextView
    private var examType: ExamType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_list)

        // Get exam type from intent
        val examTypeString = intent.getStringExtra("EXAM_TYPE")
        examType = examTypeString?.let { ExamType.valueOf(it) }

        if (examType == null) {
            Toast.makeText(this, "Lỗi: Không xác định loại đề thi", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[ExamViewModel::class.java]

        initViews()
        setupRecyclerView()
        observeViewModel()

        // Load exams by type
        viewModel.getExamsByType(examType!!)
    }

    private fun initViews() {
        rvExams = findViewById(R.id.rvExams)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        tvTitle = findViewById(R.id.tvExamListTitle)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getExamTypeTitle(examType!!)

        tvTitle.text = getExamTypeTitle(examType!!)
    }

    private fun setupRecyclerView() {
        examAdapter = ExamAdapter { exam ->
            // Navigate to exam detail
            val intent = Intent(this, ExamDetailActivity::class.java)
            intent.putExtra("EXAM_ID", exam.id)
            startActivity(intent)
        }

        rvExams.apply {
            layoutManager = LinearLayoutManager(this@ExamListActivity)
            adapter = examAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.examsLiveData.observe(this) { result ->
            result.onSuccess { exams ->
                if (exams.isEmpty()) {
                    showEmptyState()
                } else {
                    showExams(exams)
                }
            }
            result.onFailure { error ->
                Toast.makeText(
                    this,
                    "Lỗi: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                showEmptyState()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showExams(exams: List<com.example.fe.model.ExamResponse>) {
        examAdapter.submitList(exams)
        rvExams.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
    }

    private fun showEmptyState() {
        rvExams.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
    }

    private fun getExamTypeTitle(type: ExamType): String {
        return when (type) {
            ExamType.TOEIC_FULL_TEST -> "TOEIC Full Test"
            ExamType.TOEIC_MINI_TEST -> "TOEIC Mini Test"
            ExamType.IELTS_ACADEMIC -> "IELTS Academic"
            ExamType.IELTS_GENERAL -> "IELTS General"
            ExamType.MOCK_TEST -> "Mock Test"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

