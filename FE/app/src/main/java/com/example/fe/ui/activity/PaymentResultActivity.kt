package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fe.MainActivity
import com.example.fe.R
import com.example.fe.viewmodel.OrderViewModel

class PaymentResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IS_SUCCESS = "EXTRA_IS_SUCCESS"
        const val EXTRA_TXN_REF = "EXTRA_TXN_REF"
        const val EXTRA_COURSE_ID = "EXTRA_COURSE_ID"
    }

    private lateinit var orderViewModel: OrderViewModel
    private lateinit var progressBar: ProgressBar
    private lateinit var ivResultIcon: ImageView
    private lateinit var tvResultTitle: TextView
    private lateinit var tvResultMessage: TextView
    private lateinit var tvTxnRef: TextView
    private lateinit var btnAction: Button
    private lateinit var btnViewOrders: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_result)

        val isSuccess = intent.getBooleanExtra(EXTRA_IS_SUCCESS, false)
        val txnRef = intent.getStringExtra(EXTRA_TXN_REF) ?: ""

        progressBar = findViewById(R.id.progressBarResult)
        ivResultIcon = findViewById(R.id.ivResultIcon)
        tvResultTitle = findViewById(R.id.tvResultTitle)
        tvResultMessage = findViewById(R.id.tvResultMessage)
        tvTxnRef = findViewById(R.id.tvTxnRef)
        btnAction = findViewById(R.id.btnAction)
        btnViewOrders = findViewById(R.id.btnViewOrders)

        orderViewModel = ViewModelProvider(this)[OrderViewModel::class.java]

        setupObservers()

        if (isSuccess && txnRef.isNotEmpty()) {
            showLoading()
            orderViewModel.processSuccessfulPayment(txnRef)
        } else {
            showFailureUI()
        }

        tvTxnRef.text = getString(R.string.txn_ref_label, txnRef)

        btnAction.setOnClickListener {
            navigateToMain()
        }

        btnViewOrders.setOnClickListener {
            startActivity(Intent(this, OrderListActivity::class.java))
        }

        // Handle back press with OnBackPressedCallback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToMain()
            }
        })
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun setupObservers() {
        orderViewModel.processPaymentLiveData.observe(this) { result ->
            progressBar.visibility = View.GONE
            result.onSuccess {
                showSuccessUI()
            }.onFailure { error ->
                // Even if backend call fails, show success since VNPay confirmed payment
                showSuccessUI()
                Toast.makeText(this, "Lưu ý: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        ivResultIcon.visibility = View.GONE
        tvResultTitle.setText(R.string.payment_processing)
        tvResultMessage.setText(R.string.payment_processing_message)
        btnViewOrders.visibility = View.GONE
    }

    private fun showSuccessUI() {
        progressBar.visibility = View.GONE
        ivResultIcon.visibility = View.VISIBLE
        ivResultIcon.setImageResource(R.drawable.ic_payment_success)
        tvResultTitle.setText(R.string.payment_success_title)
        tvResultMessage.setText(R.string.payment_success_message)
        btnAction.setText(R.string.go_home)
        btnViewOrders.visibility = View.VISIBLE
    }

    private fun showFailureUI() {
        progressBar.visibility = View.GONE
        ivResultIcon.visibility = View.VISIBLE
        ivResultIcon.setImageResource(R.drawable.ic_payment_failed)
        tvResultTitle.setText(R.string.payment_failed_title)
        tvResultMessage.setText(R.string.payment_failed_message)
        btnAction.setText(R.string.try_again)
        btnViewOrders.visibility = View.GONE
    }
}
