package com.example.fe.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.fe.R
import com.example.fe.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel

    // UI Components
    private lateinit var toolbar: Toolbar
    private lateinit var tvFullName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvUserId: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        setupToolbar()
        setupViewModel()
        observeViewModel()

        // Load user info
        viewModel.getMyInfo()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        tvFullName = findViewById(R.id.tvFullName)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvUserId = findViewById(R.id.tvUserId)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Hồ sơ của tôi"
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
    }

    private fun observeViewModel() {
        // Observe user info
        viewModel.userInfo.observe(this) { result ->
            result.onSuccess { userResponse ->
                // Update UI with user data
                tvFullName.text = userResponse.fullName
                tvUsername.text = getString(R.string.username_format, userResponse.username)
                tvEmail.text = userResponse.email
                tvPhone.text = userResponse.phoneNumber
                tvUserId.text = userResponse.id.toString()
            }.onFailure { error ->
                // Show error message
                Toast.makeText(
                    this,
                    error.message ?: "Không thể tải thông tin người dùng",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
