package com.example.fe.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.fe.R
import com.example.fe.model.*
import com.example.fe.viewmodel.DocumentViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DocumentEditorActivity : AppCompatActivity() {

    private val viewModel: DocumentViewModel by viewModels()
    
    private lateinit var titleInput: TextInputEditText
    private lateinit var titleLayout: TextInputLayout
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var descriptionLayout: TextInputLayout
    private lateinit var contentInput: TextInputEditText
    private lateinit var contentLayout: TextInputLayout
    private lateinit var thumbnailInput: TextInputEditText
    private lateinit var categorySpinner: Spinner
    private lateinit var publishedSwitch: SwitchMaterial
    private lateinit var progressIndicator: CircularProgressIndicator

    private var isEditMode = false
    private var documentId: Long = 0
    private var currentDocument: DocumentResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_editor)

        // Kiểm tra quyền trước khi cho phép truy cập
        if (!UserSession.isLoggedIn()) {
            showError("Bạn cần đăng nhập để tiếp tục")
            finish()
            return
        }

        isEditMode = intent.getBooleanExtra("edit_mode", false)
        documentId = intent.getLongExtra("document_id", 0)

        if (isEditMode && !UserSession.canEdit()) {
            showError("Bạn không có quyền chỉnh sửa tài liệu")
            finish()
            return
        }

        if (!isEditMode && !UserSession.canCreate()) {
            showError("Bạn không có quyền tạo tài liệu")
            finish()
            return
        }

        setupToolbar()
        setupViews()
        setupObservers()
        setupCategorySpinner()

        if (isEditMode && documentId > 0) {
            viewModel.loadDocumentById(documentId)
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = if (isEditMode) "Chỉnh sửa tài liệu" else "Tạo tài liệu mới"
        }
    }

    private fun setupViews() {
        titleInput = findViewById(R.id.editTextTitle)
        titleLayout = findViewById(R.id.textInputLayoutTitle)
        descriptionInput = findViewById(R.id.editTextDescription)
        descriptionLayout = findViewById(R.id.textInputLayoutDescription)
        contentInput = findViewById(R.id.editTextContent)
        contentLayout = findViewById(R.id.textInputLayoutContent)
        thumbnailInput = findViewById(R.id.editTextThumbnail)
        categorySpinner = findViewById(R.id.spinnerCategory)
        publishedSwitch = findViewById(R.id.switchPublished)
        progressIndicator = findViewById(R.id.progressIndicator)
    }

    private fun setupObservers() {
        viewModel.selectedDocument.observe(this, Observer { document ->
            document?.let {
                currentDocument = it
                populateFields(it)
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
                finish() // Quay lại sau khi lưu thành công
            }
        })
    }

    private fun setupCategorySpinner() {
        val categories = DocCategory.values().map { getCategoryDisplayName(it.name) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    private fun populateFields(document: DocumentResponse) {
        titleInput.setText(document.title)
        descriptionInput.setText(document.description)
        contentInput.setText(document.content)
        thumbnailInput.setText(document.thumbnail)
        publishedSwitch.isChecked = document.isPublished
        
        // Set category spinner
        val categoryPosition = DocCategory.values().indexOf(document.category)
        if (categoryPosition >= 0) {
            categorySpinner.setSelection(categoryPosition)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_document_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                confirmDiscardChanges()
                true
            }
            R.id.action_save_draft -> {
                saveDocument(isPublished = false)
                true
            }
            R.id.action_publish -> {
                saveDocument(isPublished = true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveDocument(isPublished: Boolean) {
        // Clear previous errors
        clearErrors()

        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val content = contentInput.text.toString().trim()
        val thumbnail = thumbnailInput.text.toString().trim()
        val categoryPosition = categorySpinner.selectedItemPosition
        
        // Validation
        var hasError = false
        
        if (title.isEmpty()) {
            titleLayout.error = "Tiêu đề không được để trống"
            hasError = true
        } else if (title.length > 255) {
            titleLayout.error = "Tiêu đề quá dài (tối đa 255 ký tự)"
            hasError = true
        }
        
        if (description.length > 500) {
            descriptionLayout.error = "Mô tả không quá 500 ký tự"
            hasError = true
        }
        
        if (content.isEmpty()) {
            contentLayout.error = "Nội dung không được để trống"
            hasError = true
        }
        
        if (hasError) {
            return
        }

        val category = DocCategory.values()[categoryPosition]
        val request = DocumentRequest(
            title = title,
            description = description.ifEmpty { null },
            thumbnail = thumbnail.ifEmpty { null },
            content = content,
            category = category,
            isPublished = isPublished
        )

        if (isEditMode) {
            viewModel.updateDocument(documentId, request)
        } else {
            viewModel.createDocument(request)
        }
    }

    private fun clearErrors() {
        titleLayout.error = null
        descriptionLayout.error = null
        contentLayout.error = null
    }

    private fun confirmDiscardChanges() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(this)
                .setTitle("Hủy thay đổi")
                .setMessage("Bạn có thay đổi chưa được lưu. Bạn có muốn thoát mà không lưu?")
                .setPositiveButton("Thoát") { _, _ ->
                    finish()
                }
                .setNegativeButton("Tiếp tục chỉnh sửa", null)
                .show()
        } else {
            finish()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        if (!isEditMode) {
            // Mode tạo mới - kiểm tra có input nào không
            return titleInput.text.toString().isNotEmpty() ||
                   descriptionInput.text.toString().isNotEmpty() ||
                   contentInput.text.toString().isNotEmpty() ||
                   thumbnailInput.text.toString().isNotEmpty()
        } else {
            // Mode edit - so sánh với dữ liệu gốc
            currentDocument?.let { document ->
                return titleInput.text.toString() != document.title ||
                       descriptionInput.text.toString() != document.description ||
                       contentInput.text.toString() != document.content ||
                       thumbnailInput.text.toString() != document.thumbnail ||
                       publishedSwitch.isChecked != document.isPublished ||
                       DocCategory.values()[categorySpinner.selectedItemPosition] != document.category
            }
        }
        return false
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

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}