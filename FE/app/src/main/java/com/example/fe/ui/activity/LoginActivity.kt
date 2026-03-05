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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Configure Google Sign-In (use Web Client ID from your backend)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogleLogin = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnGoogleLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        // Observer for normal login
        viewModel.loginResult.observe(this) { result ->
            Log.d(TAG, "Observer triggered")
            result.onSuccess { authResponse ->
                Log.d(TAG, "Login success, navigating")
                TokenManager.saveToken(this, authResponse.token)
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            result.onFailure { exception ->
                Log.e(TAG, "Login failed: ${exception.message}", exception)
                Toast.makeText(this, exception.message ?: "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        // Observer for Google login
        viewModel.googleLoginResult.observe(this) { result ->
            result.onSuccess { authResponse ->
                Log.d(TAG, "Google login success, navigating")
                TokenManager.saveToken(this, authResponse.token)
                Toast.makeText(this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            result.onFailure { exception ->
                Log.e(TAG, "Google login failed: ${exception.message}", exception)
                Toast.makeText(this, exception.message ?: "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            Log.d(TAG, "Login button clicked - Username: '$username'")
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.login(username, password)
        }

        btnGoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    Log.d(TAG, "Google sign-in succeeded, sending token to backend")
                    viewModel.loginWithGoogle(idToken)
                } else {
                    Log.e(TAG, "Google ID Token is null")
                    Toast.makeText(this, "Không lấy được token Google", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e(TAG, "Google sign-in failed: ${e.statusCode}", e)
                Toast.makeText(this, "Đăng nhập Google thất bại: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
