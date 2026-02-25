package com.example.fe.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fe.R

class PaymentWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar

    companion object {
        const val EXTRA_PAYMENT_URL = "EXTRA_PAYMENT_URL"
        const val EXTRA_COURSE_ID = "EXTRA_COURSE_ID"
        const val DEEP_LINK_SCHEME = "studymate"
        const val DEEP_LINK_HOST = "payment-success"
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_webview)

        val toolbar = findViewById<Toolbar>(R.id.toolbarPayment)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Thanh toán VNPay"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        webView = findViewById(R.id.webViewPayment)
        progressBar = findViewById(R.id.progressBarPayment)

        val paymentUrl = intent.getStringExtra(EXTRA_PAYMENT_URL) ?: run {
            finish()
            return
        }
        val courseId = intent.getLongExtra(EXTRA_COURSE_ID, -1L)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.visibility = View.GONE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url ?: return false
                return handleUrl(url, courseId)
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val uri = Uri.parse(url ?: return false)
                return handleUrl(uri, courseId)
            }
        }

        webView.loadUrl(paymentUrl)

        // Handle back press — navigate within WebView or close
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun handleUrl(uri: Uri, courseId: Long): Boolean {
        // Intercept deep link: studymate://payment-success?vnp_ResponseCode=00&vnp_TxnRef=...
        if (uri.scheme == DEEP_LINK_SCHEME && uri.host == DEEP_LINK_HOST) {
            val responseCode = uri.getQueryParameter("vnp_ResponseCode") ?: ""
            val txnRef = uri.getQueryParameter("vnp_TxnRef") ?: ""
            val isSuccess = responseCode == "00"

            val intent = Intent(this, PaymentResultActivity::class.java).apply {
                putExtra(PaymentResultActivity.EXTRA_IS_SUCCESS, isSuccess)
                putExtra(PaymentResultActivity.EXTRA_TXN_REF, txnRef)
                putExtra(PaymentResultActivity.EXTRA_COURSE_ID, courseId)
            }
            startActivity(intent)
            finish()
            return true
        }
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
