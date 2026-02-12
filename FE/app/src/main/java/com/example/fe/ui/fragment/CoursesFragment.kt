package com.example.fe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fe.R
import com.example.fe.ui.activity.CoursesListActivity

class CoursesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_courses, container, false)
        
        // Automatically open CoursesListActivity when this fragment loads
        startActivity(Intent(requireContext(), CoursesListActivity::class.java))
        
        return view
    }
}

