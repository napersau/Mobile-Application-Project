package com.example.fe.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fe.R

class FlashcardsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // This fragment is no longer used as a launcher.
        // DecksListActivity is opened directly from MainActivity.
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}
