package com.example.fe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fe.R
import com.example.fe.ui.activity.DocumentCategoryActivity

class DocumentsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_documents, container, false)
        
        // Setup click listener to navigate to DocumentCategoryActivity
        view.setOnClickListener {
            startActivity(Intent(requireContext(), DocumentCategoryActivity::class.java))
        }
        
        return view
    }
}
