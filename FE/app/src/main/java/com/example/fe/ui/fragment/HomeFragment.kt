package com.example.fe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.fe.R
import com.example.fe.ui.activity.CoursesListActivity
import com.example.fe.ui.activity.DecksListActivity
import com.example.fe.ui.activity.DocumentCategoryActivity
import com.example.fe.ui.activity.ExamCategoryActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup click listeners for feature cards
        view.findViewById<CardView>(R.id.cardCourses)?.setOnClickListener {
            startActivity(Intent(requireContext(), CoursesListActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardFlashcards)?.setOnClickListener {
            startActivity(Intent(requireContext(), DecksListActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardDocuments)?.setOnClickListener {
            startActivity(Intent(requireContext(), DocumentCategoryActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardExams)?.setOnClickListener {
            startActivity(Intent(requireContext(), ExamCategoryActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardTranslate)?.setOnClickListener {
            // Navigate to TranslateFragment and update bottom nav selection
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav?.selectedItemId = R.id.navigation_ai
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TranslateFragment())
                .commit()
        }
    }
}

