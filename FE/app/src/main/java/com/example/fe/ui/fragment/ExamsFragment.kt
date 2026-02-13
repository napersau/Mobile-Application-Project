package com.example.fe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.fe.R
import com.example.fe.model.ExamType
import com.example.fe.ui.activity.ExamListActivity

class ExamsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryCards(view)
    }

    private fun setupCategoryCards(view: View) {
        view.findViewById<CardView>(R.id.cardToeicFull)?.setOnClickListener {
            navigateToExamList(ExamType.TOEIC_FULL_TEST)
        }

        view.findViewById<CardView>(R.id.cardToeicMini)?.setOnClickListener {
            navigateToExamList(ExamType.TOEIC_MINI_TEST)
        }

        view.findViewById<CardView>(R.id.cardIeltsAcademic)?.setOnClickListener {
            navigateToExamList(ExamType.IELTS_ACADEMIC)
        }

        view.findViewById<CardView>(R.id.cardIeltsGeneral)?.setOnClickListener {
            navigateToExamList(ExamType.IELTS_GENERAL)
        }

        view.findViewById<CardView>(R.id.cardMockTest)?.setOnClickListener {
            navigateToExamList(ExamType.MOCK_TEST)
        }
    }

    private fun navigateToExamList(examType: ExamType) {
        val intent = Intent(requireContext(), ExamListActivity::class.java)
        intent.putExtra("EXAM_TYPE", examType.name)
        startActivity(intent)
    }
}

