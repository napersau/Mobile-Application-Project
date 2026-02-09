package com.example.fe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fe.repository.DocumentRepository

class DocumentViewModelFactory(
    private val repository: DocumentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocumentViewModel::class.java)) {
            return DocumentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// Usage example trong Activity:
/*
class DocumentListActivity : AppCompatActivity() {
    private val viewModel: DocumentViewModel by viewModels {
        DocumentViewModelFactory(
            DocumentRepository(NetworkManager.documentApiService)
        )
    }
}
*/