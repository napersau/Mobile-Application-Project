package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.DocumentResponse
import com.example.fe.model.UserSession
import com.example.fe.ui.adapter.DocumentAdapter
import com.example.fe.viewmodel.DocumentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator

class DocumentListActivity : AppCompatActivity() {

    private val viewModel: DocumentViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DocumentAdapter
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_list)

        setupToolbar()
        setupViews()
        setupObservers()
        setupClickListeners()

        // Load documents
        viewModel.loadDocumentsByCategory()
    }

    private fun setupToolbar() {
        supportActionBar?.title = "Tài liệu học tập"
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerViewDocuments)
        fabAdd = findViewById(R.id.fabAddDocument)
        progressIndicator = findViewById(R.id.progressIndicator)

        adapter = DocumentAdapter(
            onItemClick = { document ->
                // Mở tài liệu để đọc
                openDocument(document)
            },
            onEditClick = { document ->
                // Chỉ admin mới thấy nút này
                editDocument(document)
            },
            onDeleteClick = { document ->
                // Chỉ admin mới thấy nút này
                confirmDeleteDocument(document)
            },
            isAdmin = UserSession.isAdmin()
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DocumentListActivity)
            adapter = this@DocumentListActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.documents.observe(this, Observer { documents ->
            adapter.submitList(documents)
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
            }
        })

        viewModel.canCreate.observe(this, Observer { canCreate ->
            fabAdd.visibility = if (canCreate) View.VISIBLE else View.GONE
        })

        // Update adapter when permissions change
        viewModel.isAdmin.observe(this, Observer { isAdmin ->
            adapter.updateAdminStatus(isAdmin)
        })
    }

    private fun setupClickListeners() {
        fabAdd.setOnClickListener {
            if (UserSession.canCreate()) {
                createDocument()
            } else {
                showError("Bạn không có quyền tạo tài liệu")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_document_list, menu)
        
        // Ẩn/hiện menu items dựa trên quyền
        menu?.findItem(R.id.action_admin_panel)?.isVisible = UserSession.isAdmin()
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.loadDocumentsByCategory()
                true
            }
            R.id.action_admin_panel -> {
                if (UserSession.isAdmin()) {
                    openAdminPanel()
                }
                true
            }
            R.id.action_profile -> {
                openProfile()
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openDocument(document: DocumentResponse) {
        val intent = Intent(this, DocumentDetailActivity::class.java).apply {
            putExtra("document_id", document.id)
            putExtra("document_title", document.title)
        }
        startActivity(intent)
    }

    private fun createDocument() {
        if (!UserSession.canCreate()) {
            showError("Bạn không có quyền tạo tài liệu")
            return
        }
        
        val intent = Intent(this, DocumentEditorActivity::class.java)
        startActivity(intent)
    }

    private fun editDocument(document: DocumentResponse) {
        if (!UserSession.canEdit()) {
            showError("Bạn không có quyền chỉnh sửa tài liệu")
            return
        }

        val intent = Intent(this, DocumentEditorActivity::class.java).apply {
            putExtra("document_id", document.id)
            putExtra("edit_mode", true)
        }
        startActivity(intent)
    }

    private fun confirmDeleteDocument(document: DocumentResponse) {
        if (!UserSession.canDelete()) {
            showError("Bạn không có quyền xóa tài liệu")
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa tài liệu \"${document.title}\"?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteDocument(document.id)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun openAdminPanel() {
        val intent = Intent(this, AdminPanelActivity::class.java)
        startActivity(intent)
    }

    private fun openProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất?")
            .setPositiveButton("Đăng xuất") { _, _ ->
                UserSession.logout()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Update permissions khi quay lại activity
        viewModel.updateUserPermissions()
        // Refresh data
        viewModel.loadDocumentsByCategory()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}