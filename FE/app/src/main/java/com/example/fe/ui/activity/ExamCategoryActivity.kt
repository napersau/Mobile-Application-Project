package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fe.R
import com.example.fe.model.ExamType

class ExamCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_category)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chọn loại đề thi"

        setupCategoryCards()
    }

    private fun setupCategoryCards() {
        findViewById<CardView>(R.id.cardToeicFull).setOnClickListener {
            navigateToExamList(ExamType.TOEIC_FULL_TEST)
        }

        findViewById<CardView>(R.id.cardToeicMini).setOnClickListener {
            navigateToExamList(ExamType.TOEIC_MINI_TEST)
        }

        findViewById<CardView>(R.id.cardIeltsAcademic).setOnClickListener {
            navigateToExamList(ExamType.IELTS_ACADEMIC)
        }

        findViewById<CardView>(R.id.cardIeltsGeneral).setOnClickListener {
            navigateToExamList(ExamType.IELTS_GENERAL)
        }

        findViewById<CardView>(R.id.cardMockTest).setOnClickListener {
            navigateToExamList(ExamType.MOCK_TEST)
        }
    }

    private fun navigateToExamList(examType: ExamType) {
        val intent = Intent(this, ExamListActivity::class.java)
        intent.putExtra("EXAM_TYPE", examType.name)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

