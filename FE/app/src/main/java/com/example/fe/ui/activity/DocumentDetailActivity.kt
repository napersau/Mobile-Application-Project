package com.example.fe.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.fe.R
import com.example.fe.model.DocumentResponse
import com.example.fe.model.UserSession
import com.example.fe.viewmodel.DocumentViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.*

class DocumentDetailActivity : AppCompatActivity() {

    private val viewModel: DocumentViewModel by viewModels()
    private lateinit var titleText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var contentText: TextView
    private lateinit var categoryText: TextView
    private lateinit var authorText: TextView
    private lateinit var dateText: TextView
    private lateinit var viewCountText: TextView
    private lateinit var thumbnailImage: ImageView
    private lateinit var publishedIndicator: TextView
    private lateinit var progressIndicator: CircularProgressIndicator

    private var documentId: Long = 0
    private var currentDocument: DocumentResponse? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_detail)

        documentId = intent.getLongExtra("document_id", 0)
        val documentTitle = intent.getStringExtra("document_title") ?: "Tài liệu"

        setupToolbar(documentTitle)
        setupViews()
        setupObservers()

        if (documentId > 0) {
            viewModel.loadDocumentById(documentId)
        } else {
            showError("ID tài liệu không hợp lệ")
            finish()
        }
    }

    private fun setupToolbar(title: String) {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            this.title = title
        }
    }

    private fun setupViews() {
        titleText = findViewById(R.id.textTitle)
        descriptionText = findViewById(R.id.textDescription)
        contentText = findViewById(R.id.textContent)
        categoryText = findViewById(R.id.textCategory)
        authorText = findViewById(R.id.textAuthor)
        dateText = findViewById(R.id.textDate)
        viewCountText = findViewById(R.id.textViewCount)
        thumbnailImage = findViewById(R.id.imageThumbnail)
        publishedIndicator = findViewById(R.id.textPublished)
        progressIndicator = findViewById(R.id.progressIndicator)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObservers() {
        viewModel.selectedDocument.observe(this, Observer { document ->
            document?.let {
                currentDocument = it
                displayDocument(it)
                invalidateOptionsMenu() // Update menu
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let {
                showError(it)
                viewModel.clearError()
            }
        })

        viewModel.success.observe(this, Observer { success ->
            success?.let {
                showSuccess(it)
                viewModel.clearSuccess()
                finish() // Quay lại sau khi xóa thành công
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayDocument(document: DocumentResponse) {
        titleText.text = document.title
        descriptionText.text = document.description.ifEmpty { "Không có mô tả" }
        contentText.text = document.content
        categoryText.text = getCategoryDisplayName(document.category.name)
        viewCountText.text = getString(R.string.view_count_format, document.viewCount)

        // Format date - Convert Instant to Date without using Date.from()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        // Manually convert Instant to milliseconds (compatible with API 24+)
        val millis = document.updatedAt.epochSecond * 1000 + document.updatedAt.nano / 1000000
        val date = Date(millis)
        dateText.text = getString(R.string.updated_date_format, dateFormat.format(date))

        // Published status
        publishedIndicator.text = getString(if (document.isPublished) R.string.published else R.string.draft)
        publishedIndicator.setBackgroundColor(
            if (document.isPublished) 
                getColor(R.color.published_green) 
            else 
                getColor(R.color.draft_orange)
        )

        // Load thumbnail (if using image loading library)
        // Glide.with(this)
        //     .load(document.thumbnail)
        //     .placeholder(R.drawable.placeholder_document)
        //     .error(R.drawable.placeholder_document)
        //     .into(thumbnailImage)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_document_detail, menu)
        
        // Chỉ hiển thị menu admin nếu user là admin
        val editItem = menu?.findItem(R.id.action_edit)
        val deleteItem = menu?.findItem(R.id.action_delete)
        
        editItem?.isVisible = UserSession.canEdit()
        deleteItem?.isVisible = UserSession.canDelete()
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_share -> {
                shareDocument()
                true
            }
            R.id.action_edit -> {
                editDocument()
                true
            }
            R.id.action_delete -> {
                confirmDeleteDocument()
                true
            }
            R.id.action_refresh -> {
                viewModel.loadDocumentById(documentId)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareDocument() {
        currentDocument?.let { document ->
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, document.title)
                putExtra(Intent.EXTRA_TEXT, """
                    ${document.title}
                    
                    ${document.description}
                    
                    Danh mục: ${getCategoryDisplayName(document.category.name)}
                    Lượt xem: ${document.viewCount}
                """.trimIndent())
            }
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ tài liệu"))
        }
    }

    private fun editDocument() {
        if (!UserSession.canEdit()) {
            showError("Bạn không có quyền chỉnh sửa tài liệu")
            return
        }

        currentDocument?.let { document ->
            val intent = Intent(this, DocumentEditorActivity::class.java).apply {
                putExtra("document_id", document.id)
                putExtra("edit_mode", true)
            }
            startActivity(intent)
        }
    }

    private fun confirmDeleteDocument() {
        if (!UserSession.canDelete()) {
            showError("Bạn không có quyền xóa tài liệu")
            return
        }

        currentDocument?.let { document ->
            AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa tài liệu \"${document.title}\"?\\n\\nHành động này không thể hoàn tác.")
                .setPositiveButton("Xóa") { _, _ ->
                    viewModel.deleteDocument(document.id)
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    private fun getCategoryDisplayName(category: String): String {
        return when (category) {
            "TECHNOLOGY" -> "Công nghệ"
            "SCIENCE" -> "Khoa học"
            "MATHEMATICS" -> "Toán học"
            "LITERATURE" -> "Văn học"
            "HISTORY" -> "Lịch sử"
            "LANGUAGE" -> "Ngôn ngữ"
            "BUSINESS" -> "Kinh doanh"
            "HEALTH" -> "Sức khỏe"
            "OTHER" -> "Khác"
            else -> category
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh document when returning from edit
        viewModel.loadDocumentById(documentId)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}