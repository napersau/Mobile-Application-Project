package com.example.fe.utils

import android.util.Log
import com.example.fe.repository.AnalyticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Tracks study time for any learning screen.
 *
 * Usage in Activity:
 *   private val studyTimeTracker = StudyTimeTracker()
 *   override fun onResume() { super.onResume(); studyTimeTracker.start() }
 *   override fun onPause() { super.onPause(); studyTimeTracker.stop() }
 *
 * Usage in Fragment:
 *   override fun onResume() { super.onResume(); studyTimeTracker.start() }
 *   override fun onPause() { super.onPause(); studyTimeTracker.stop() }
 */
class StudyTimeTracker(
    private val tag: String = "StudyTimeTracker"
) {
    private val repository = AnalyticsRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var startTimeMs: Long = 0L
    private var isRunning = false

    /** Call in onResume() */
    fun start() {
        startTimeMs = System.currentTimeMillis()
        isRunning = true
        Log.d(tag, "Study timer started")
    }

    /**
     * Call in onPause(). Sends elapsed seconds to backend if >= [minSeconds].
     * @param minSeconds minimum seconds to consider a valid study session (default 3s)
     */
    fun stop(minSeconds: Long = 3L) {
        if (!isRunning) return
        isRunning = false

        val elapsedSeconds = (System.currentTimeMillis() - startTimeMs) / 1000L
        Log.d(tag, "Study timer stopped. Elapsed: ${elapsedSeconds}s")

        if (elapsedSeconds >= minSeconds) {
            scope.launch {
                try {
                    repository.recordStudyTime(elapsedSeconds)
                    Log.d(tag, "Study time recorded: ${elapsedSeconds}s")
                } catch (e: Exception) {
                    Log.w(tag, "Failed to record study time: ${e.message}")
                }
            }
        } else {
            Log.d(tag, "Session too short (${elapsedSeconds}s < ${minSeconds}s), not recorded")
        }
    }
}

