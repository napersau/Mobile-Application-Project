package com.example.fe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fe.R
import com.example.fe.ui.activity.DecksListActivity

class FlashcardsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_flashcards, container, false)
        
        // Setup click listener to navigate to DecksListActivity
        view.setOnClickListener {
            startActivity(Intent(requireContext(), DecksListActivity::class.java))
        }
        
        return view
    }
}
