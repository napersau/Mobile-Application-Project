package com.example.fe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fe.ui.activity.DecksListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup click listeners for feature cards
        findViewById<CardView>(R.id.cardVocabulary).setOnClickListener {
            startActivity(Intent(this, DecksListActivity::class.java))
        }

        findViewById<CardView>(R.id.cardGrammar).setOnClickListener {
            // TODO: Navigate to Grammar Activity
        }

        findViewById<CardView>(R.id.cardListening).setOnClickListener {
            // TODO: Navigate to Listening Activity
        }

        findViewById<CardView>(R.id.cardSpeaking).setOnClickListener {
            // TODO: Navigate to Speaking Activity
        }

        findViewById<CardView>(R.id.cardExercise).setOnClickListener {
            // TODO: Navigate to Exercise Activity
        }

        findViewById<CardView>(R.id.cardProgress).setOnClickListener {
            // TODO: Navigate to Progress Activity
        }
    }
}