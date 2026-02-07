package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fe.MainActivity
import com.example.fe.R
import com.example.fe.utils.TokenManager
import com.example.fe.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        // Setup observer TRƯỚC khi có thể login
        viewModel.loginResult.observe(this) { result ->
            Log.d("LoginActivity", "Observer triggered")
            result.onSuccess { authResponse ->
                Log.d("LoginActivity", "Login success, showing toast and navigating")
                
                // LƯU TOKEN vào SharedPreferences
                TokenManager.saveTokens(
                    this,
                    authResponse.accessToken,
                    authResponse.refreshToken
                )
                
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            result.onFailure { exception ->
                Log.e("LoginActivity", "Login failed: ${exception.message}", exception)
                Toast.makeText(
                    this,
                    exception.message ?: "Đăng nhập thất bại",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()
            
            Log.d("LoginActivity", "Login button clicked: $username")
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            viewModel.login(username, password)
        }

        tvSignUp.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }
}
