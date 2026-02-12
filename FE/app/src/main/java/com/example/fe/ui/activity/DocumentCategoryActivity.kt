package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fe.R
import com.example.fe.model.DocCategory

class DocumentCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_category)

        setupToolbar()
        setupCategoryCards()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "Chọn Danh Mục Tài Liệu"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupCategoryCards() {
        findViewById<CardView>(R.id.cardGrammar).setOnClickListener {
            openDocumentList(DocCategory.GRAMMAR.name)
        }

        findViewById<CardView>(R.id.cardVocabulary).setOnClickListener {
            openDocumentList(DocCategory.VOCABULARY.name)
        }

        findViewById<CardView>(R.id.cardSkills).setOnClickListener {
            openDocumentList(DocCategory.SKILLS.name)
        }

        findViewById<CardView>(R.id.cardExamPreparation).setOnClickListener {
            openDocumentList(DocCategory.EXAM_PREPARATION.name)
        }
    }

    private fun openDocumentList(category: String) {
        val intent = Intent(this, DocumentListActivity::class.java).apply {
            putExtra("category", category)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

