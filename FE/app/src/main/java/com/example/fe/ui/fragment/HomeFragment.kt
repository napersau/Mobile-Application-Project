package com.example.fe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.fe.R
import com.example.fe.ui.activity.DecksListActivity
import com.example.fe.ui.activity.DocumentCategoryActivity

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

        view.findViewById<CardView>(R.id.cardVocabulary).setOnClickListener {
            startActivity(Intent(requireContext(), DecksListActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardGrammar).setOnClickListener {
            startActivity(Intent(requireContext(), DocumentCategoryActivity::class.java))
        }

        view.findViewById<CardView>(R.id.cardListening).setOnClickListener {
            // TODO: Navigate to Listening Activity
        }

        view.findViewById<CardView>(R.id.cardSpeaking).setOnClickListener {
            // TODO: Navigate to Speaking Activity
        }

        view.findViewById<CardView>(R.id.cardExercise).setOnClickListener {
            // TODO: Navigate to Exercise Activity
        }

        view.findViewById<CardView>(R.id.cardProgress).setOnClickListener {
            // TODO: Navigate to Progress Activity
        }
    }
}
