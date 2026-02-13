package com.example.fe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.ui.adapter.ChatAdapter
import com.example.fe.viewmodel.AIViewModel

class AIFragment : Fragment() {

    private lateinit var viewModel: AIViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var rvChatMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnClearChat: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AIViewModel::class.java]

        initViews(view)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun initViews(view: View) {
        rvChatMessages = view.findViewById(R.id.rvChatMessages)
        etMessage = view.findViewById(R.id.etMessage)
        btnSend = view.findViewById(R.id.btnSend)
        btnClearChat = view.findViewById(R.id.btnClearChat)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        rvChatMessages.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true // Start from bottom
            }
            adapter = chatAdapter
        }
    }

    private fun setupListeners() {
        btnSend.setOnClickListener {
            sendMessage()
        }

        etMessage.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }

        btnClearChat.setOnClickListener {
            viewModel.clearChat()
        }
    }

    private fun sendMessage() {
        val message = etMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            viewModel.sendMessage(message)
            etMessage.text.clear()
        }
    }

    private fun observeViewModel() {
        viewModel.chatMessages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages.toList())
            // Scroll to bottom
            if (messages.isNotEmpty()) {
                rvChatMessages.smoothScrollToPosition(messages.size - 1)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSend.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

