package com.example.fe.ui.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
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
    private lateinit var resultScrollView: View
    private lateinit var progressBar: View
    private lateinit var btnCopy: ImageButton
    private lateinit var btnShare: ImageButton
    private lateinit var btnClearInput: ImageButton
    private lateinit var tvCharCount: TextView
    private lateinit var tvSourceLang: TextView
    private lateinit var tvTargetLang: TextView
    private lateinit var btnSwapLanguage: ImageButton
    private lateinit var layoutLoading: LinearLayout
    private lateinit var layoutEmptyResult: LinearLayout
    private lateinit var resultActionBar: LinearLayout

    // Track swap state
    private var isSwapped = false

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
        setupListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        etInputText = view.findViewById(R.id.etInputText)
        btnTranslate = view.findViewById(R.id.btnTranslate)
        tvTranslationResult = view.findViewById(R.id.tvTranslationResult)
        resultCard = view.findViewById(R.id.resultCard)
        resultScrollView = view.findViewById(R.id.resultScrollView)
        progressBar = view.findViewById(R.id.progressBar)
        btnCopy = view.findViewById(R.id.btnCopy)
        btnShare = view.findViewById(R.id.btnShare)
        btnClearInput = view.findViewById(R.id.btnClearInput)
        tvCharCount = view.findViewById(R.id.tvCharCount)
        tvSourceLang = view.findViewById(R.id.tvSourceLang)
        tvTargetLang = view.findViewById(R.id.tvTargetLang)
        btnSwapLanguage = view.findViewById(R.id.btnSwapLanguage)
        layoutLoading = view.findViewById(R.id.layoutLoading)
        layoutEmptyResult = view.findViewById(R.id.layoutEmptyResult)
        resultActionBar = view.findViewById(R.id.resultActionBar)
    }

    private fun setupListeners() {
        // Translate button
        btnTranslate.setOnClickListener {
            val text = etInputText.text.toString().trim()
            if (text.isNotEmpty()) {
                viewModel.translate(text)
            } else {
                Toast.makeText(requireContext(), getString(R.string.translate_empty_input), Toast.LENGTH_SHORT).show()
            }
        }

        // Copy result
        btnCopy.setOnClickListener {
            val result = tvTranslationResult.text.toString()
            if (result.isNotEmpty()) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("translation", result)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), getString(R.string.translate_copied), Toast.LENGTH_SHORT).show()
            }
        }

        // Share result
        btnShare.setOnClickListener {
            val result = tvTranslationResult.text.toString()
            if (result.isNotEmpty()) {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, result)
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.translate_share_chooser)))
            }
        }

        // Clear input
        btnClearInput.setOnClickListener {
            etInputText.setText("")
            tvTranslationResult.text = ""
            resultScrollView.visibility = View.GONE
            layoutEmptyResult.visibility = View.VISIBLE
            resultActionBar.visibility = View.GONE
        }

        // Swap languages
        btnSwapLanguage.setOnClickListener {
            isSwapped = !isSwapped
            if (isSwapped) {
                tvSourceLang.text = getString(R.string.translate_target_lang)
                tvTargetLang.text = getString(R.string.translate_source_lang)
            } else {
                tvSourceLang.text = getString(R.string.translate_source_lang)
                tvTargetLang.text = getString(R.string.translate_target_lang)
            }
            // If there's a result, put it in input and re-translate
            val currentResult = tvTranslationResult.text.toString()
            if (currentResult.isNotEmpty()) {
                etInputText.setText(currentResult)
                etInputText.setSelection(currentResult.length)
            }
        }

        // Character counter
        etInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val length = s?.length ?: 0
                tvCharCount.text = if (length > 0) "$length" else ""
                btnClearInput.visibility = if (length > 0) View.VISIBLE else View.GONE
            }
        })

        // Quick example chips
        requireView().findViewById<TextView>(R.id.tvExample1).setOnClickListener {
            etInputText.setText(getString(R.string.translate_example1))
            etInputText.setSelection(etInputText.text.length)
        }
        requireView().findViewById<TextView>(R.id.tvExample2).setOnClickListener {
            etInputText.setText(getString(R.string.translate_example2))
            etInputText.setSelection(etInputText.text.length)
        }
        requireView().findViewById<TextView>(R.id.tvExample3).setOnClickListener {
            etInputText.setText(getString(R.string.translate_example3))
            etInputText.setSelection(etInputText.text.length)
        }
    }

    private fun observeViewModel() {
        viewModel.translationResult.observe(viewLifecycleOwner) { translation ->
            if (!translation.isNullOrEmpty()) {
                tvTranslationResult.text = translation
                resultScrollView.visibility = View.VISIBLE
                layoutEmptyResult.visibility = View.GONE
                resultActionBar.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                layoutLoading.visibility = View.VISIBLE
                layoutEmptyResult.visibility = View.GONE
                resultScrollView.visibility = View.GONE
                resultActionBar.visibility = View.GONE
                btnTranslate.isEnabled = false
                btnTranslate.text = getString(R.string.translate_btn_loading)
            } else {
                layoutLoading.visibility = View.GONE
                btnTranslate.isEnabled = true
                btnTranslate.text = getString(R.string.translate_btn)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                layoutEmptyResult.visibility = View.VISIBLE
                resultScrollView.visibility = View.GONE
            }
        }
    }
}
