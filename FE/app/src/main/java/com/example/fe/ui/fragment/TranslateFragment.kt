package com.example.fe.ui.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fe.R
import com.example.fe.viewmodel.AIViewModel

class TranslateFragment : Fragment() {

    private lateinit var viewModel: AIViewModel
    private lateinit var etInputText: EditText
    private lateinit var btnTranslate: Button
    private lateinit var tvTranslationResult: TextView
    private lateinit var resultCard: CardView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnCopy: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_translate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AIViewModel::class.java]

        initViews(view)
        setupListeners(view)
        observeViewModel()
    }

    private fun initViews(view: View) {
        etInputText = view.findViewById(R.id.etInputText)
        btnTranslate = view.findViewById(R.id.btnTranslate)
        tvTranslationResult = view.findViewById(R.id.tvTranslationResult)
        resultCard = view.findViewById(R.id.resultCard)
        progressBar = view.findViewById(R.id.progressBar)
        btnCopy = view.findViewById(R.id.btnCopy)
    }

    private fun setupListeners(view: View) {
        btnTranslate.setOnClickListener {
            val text = etInputText.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.translate(text)
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show()
            }
        }

        btnCopy.setOnClickListener {
            copyToClipboard(tvTranslationResult.text.toString())
        }

        // Quick examples
        view.findViewById<TextView>(R.id.tvExample1).setOnClickListener {
            etInputText.setText("Hello, how are you?")
        }

        view.findViewById<TextView>(R.id.tvExample2).setOnClickListener {
            etInputText.setText("I am learning English")
        }

        view.findViewById<TextView>(R.id.tvExample3).setOnClickListener {
            etInputText.setText("Thank you very much")
        }
    }

    private fun observeViewModel() {
        viewModel.translationResult.observe(viewLifecycleOwner) { translation ->
            tvTranslationResult.text = translation
            resultCard.visibility = View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnTranslate.isEnabled = !isLoading
            btnTranslate.text = if (isLoading) "Đang dịch..." else "Dịch"
            if (isLoading) {
                Toast.makeText(
                    requireContext(),
                    "Đang dịch... Có thể mất vài giây",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("translation", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Đã sao chép", Toast.LENGTH_SHORT).show()
    }
}

