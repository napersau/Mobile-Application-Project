package com.example.fe.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fe.R
import com.example.fe.viewmodel.PasswordResetViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: PasswordResetViewModel

    // Step views
    private lateinit var layoutStep1: LinearLayout
    private lateinit var layoutStep2: LinearLayout
    private lateinit var layoutStep3: LinearLayout

    // Step indicators
    private lateinit var tvStep1: TextView
    private lateinit var tvStep2: TextView
    private lateinit var tvStep3: TextView

    // Inputs
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtOtp: TextInputEditText
    private lateinit var edtNewPassword: TextInputEditText
    private lateinit var edtConfirmPassword: TextInputEditText

    // Input layouts
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilOtp: TextInputLayout
    private lateinit var tilNewPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    // Buttons
    private lateinit var btnSendOtp: MaterialButton
    private lateinit var btnVerifyOtp: MaterialButton
    private lateinit var btnResetPassword: MaterialButton
    private lateinit var tvResendOtp: TextView

    // Progress
    private lateinit var progressBar: ProgressBar

    // State
    private var currentEmail: String = ""
    private var verifiedOtp: String = ""
    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        viewModel = ViewModelProvider(this)[PasswordResetViewModel::class.java]

        initViews()
        setupClickListeners()
        setupObservers()
        updateStepIndicator(1)
    }

    private fun initViews() {
        layoutStep1 = findViewById(R.id.layoutStep1)
        layoutStep2 = findViewById(R.id.layoutStep2)
        layoutStep3 = findViewById(R.id.layoutStep3)

        tvStep1 = findViewById(R.id.tvStep1)
        tvStep2 = findViewById(R.id.tvStep2)
        tvStep3 = findViewById(R.id.tvStep3)

        edtEmail = findViewById(R.id.edtEmail)
        edtOtp = findViewById(R.id.edtOtp)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)

        tilEmail = findViewById(R.id.tilEmail)
        tilOtp = findViewById(R.id.tilOtp)
        tilNewPassword = findViewById(R.id.tilNewPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)

        btnSendOtp = findViewById(R.id.btnSendOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        tvResendOtp = findViewById(R.id.tvResendOtp)

        progressBar = findViewById(R.id.progressBar)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            if (currentStep > 1) {
                showStep(currentStep - 1)
            } else {
                finish()
            }
        }
    }

    private fun setupClickListeners() {
        btnSendOtp.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            if (email.isEmpty()) {
                tilEmail.error = "Vui lòng nhập email"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Email không hợp lệ"
                return@setOnClickListener
            }
            tilEmail.error = null
            currentEmail = email
            viewModel.sendOtp(email)
        }

        btnVerifyOtp.setOnClickListener {
            val otp = edtOtp.text.toString().trim()
            if (otp.isEmpty() || otp.length < 6) {
                tilOtp.error = "Vui lòng nhập đủ 6 chữ số OTP"
                return@setOnClickListener
            }
            tilOtp.error = null
            verifiedOtp = otp
            viewModel.verifyOtp(currentEmail, otp)
        }

        tvResendOtp.setOnClickListener {
            if (currentEmail.isNotEmpty()) {
                viewModel.sendOtp(currentEmail)
                Toast.makeText(this, "Đang gửi lại OTP...", Toast.LENGTH_SHORT).show()
            }
        }

        btnResetPassword.setOnClickListener {
            val newPassword = edtNewPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            if (newPassword.length < 8) {
                tilNewPassword.error = "Mật khẩu phải có ít nhất 8 ký tự"
                return@setOnClickListener
            }
            tilNewPassword.error = null

            if (newPassword != confirmPassword) {
                tilConfirmPassword.error = "Mật khẩu xác nhận không khớp"
                return@setOnClickListener
            }
            tilConfirmPassword.error = null

            viewModel.resetPassword(currentEmail, verifiedOtp, newPassword)
        }
    }

    private fun setupObservers() {
        viewModel.sendOtpResult.observe(this) { result ->
            setLoading(false)
            result.onSuccess {
                Toast.makeText(this, "OTP đã được gửi đến email $currentEmail", Toast.LENGTH_LONG).show()
                showStep(2)
            }
            result.onFailure {
                Toast.makeText(this, it.message ?: "Gửi OTP thất bại", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.verifyOtpResult.observe(this) { result ->
            setLoading(false)
            result.onSuccess {
                Toast.makeText(this, "OTP hợp lệ!", Toast.LENGTH_SHORT).show()
                showStep(3)
            }
            result.onFailure {
                tilOtp.error = it.message ?: "OTP không hợp lệ"
            }
        }

        viewModel.resetPasswordResult.observe(this) { result ->
            setLoading(false)
            result.onSuccess {
                Toast.makeText(this, "Mật khẩu đã được đặt lại thành công!", Toast.LENGTH_LONG).show()
                finish()
            }
            result.onFailure {
                Toast.makeText(this, it.message ?: "Đặt lại mật khẩu thất bại", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showStep(step: Int) {
        currentStep = step
        layoutStep1.visibility = if (step == 1) View.VISIBLE else View.GONE
        layoutStep2.visibility = if (step == 2) View.VISIBLE else View.GONE
        layoutStep3.visibility = if (step == 3) View.VISIBLE else View.GONE
        updateStepIndicator(step)

        if (step == 2) {
            val tvOtpDesc = findViewById<TextView>(R.id.tvOtpDesc)
            tvOtpDesc.text = "Mã OTP đã được gửi đến\n$currentEmail"
        }
    }

    private fun updateStepIndicator(activeStep: Int) {
        val primaryColor = resources.getColor(R.color.primary, theme)
        val dividerColor = resources.getColor(R.color.divider, theme)
        val whiteColor = Color.WHITE
        val greyTextColor = resources.getColor(R.color.text_secondary, theme)

        // Reset all steps
        listOf(tvStep1, tvStep2, tvStep3).forEachIndexed { index, tv ->
            val stepNum = index + 1
            if (stepNum < activeStep) {
                // Completed steps
                tv.setBackgroundColor(primaryColor)
                tv.setTextColor(whiteColor)
            } else if (stepNum == activeStep) {
                // Current step
                tv.setBackgroundColor(primaryColor)
                tv.setTextColor(whiteColor)
            } else {
                // Future steps
                tv.setBackgroundColor(dividerColor)
                tv.setTextColor(greyTextColor)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnSendOtp.isEnabled = !loading
        btnVerifyOtp.isEnabled = !loading
        btnResetPassword.isEnabled = !loading
    }
}

