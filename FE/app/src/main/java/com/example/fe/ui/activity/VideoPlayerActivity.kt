package com.example.fe.ui.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.fe.R

class VideoPlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_VIDEO_URL = "VIDEO_URL"
        const val EXTRA_LESSON_TITLE = "LESSON_TITLE"
    }

    private lateinit var webView: WebView
    private lateinit var videoView: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLessonTitle: TextView
    private lateinit var noVideoLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL)
        val lessonTitle = intent.getStringExtra(EXTRA_LESSON_TITLE) ?: "Bài học"

        setupToolbar(lessonTitle)
        setupViews(lessonTitle)
        loadVideo(videoUrl)
    }

    private fun setupToolbar(title: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(title)
        }
    }

    private fun setupViews(title: String) {
        webView = findViewById(R.id.webView)
        videoView = findViewById(R.id.videoView)
        progressBar = findViewById(R.id.progressBar)
        tvLessonTitle = findViewById(R.id.tvLessonTitle)
        noVideoLayout = findViewById(R.id.noVideoLayout)
        tvLessonTitle.text = title
    }

    private fun isYouTubeUrl(url: String): Boolean =
        url.contains("youtube.com") || url.contains("youtu.be")

    /** Extract YouTube video ID from various YouTube URL formats */
    private fun extractYouTubeId(url: String): String? {
        // youtu.be/VIDEO_ID
        val shortRegex = Regex("youtu\\.be/([A-Za-z0-9_-]{11})")
        shortRegex.find(url)?.groupValues?.get(1)?.let { return it }
        // youtube.com/watch?v=VIDEO_ID
        val longRegex = Regex("[?&]v=([A-Za-z0-9_-]{11})")
        longRegex.find(url)?.groupValues?.get(1)?.let { return it }
        // youtube.com/embed/VIDEO_ID
        val embedRegex = Regex("youtube\\.com/embed/([A-Za-z0-9_-]{11})")
        embedRegex.find(url)?.groupValues?.get(1)?.let { return it }
        return null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadVideo(videoUrl: String?) {
        if (videoUrl.isNullOrEmpty()) {
            progressBar.visibility = View.GONE
            noVideoLayout.visibility = View.VISIBLE
            return
        }

        if (isYouTubeUrl(videoUrl)) {
            val videoId = extractYouTubeId(videoUrl)
            if (videoId == null) {
                progressBar.visibility = View.GONE
                noVideoLayout.visibility = View.VISIBLE
                return
            }

            // Show WebView, hide VideoView
            webView.visibility = View.VISIBLE
            videoView.visibility = View.GONE

            val settings: WebSettings = webView.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true

            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?, request: WebResourceRequest?
                ): Boolean = false  // keep navigation inside WebView

                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                }
            }

            // Use YouTube IFrame embed HTML for best compatibility
            val embedHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                  * { margin: 0; padding: 0; background: #000; }
                  body { background: #000; }
                  .video-container {
                    position: relative;
                    width: 100%;
                    padding-bottom: 56.25%;
                    height: 0;
                  }
                  .video-container iframe {
                    position: absolute;
                    top: 0; left: 0;
                    width: 100%; height: 100%;
                    border: 0;
                  }
                </style>
                </head>
                <body>
                <div class="video-container">
                  <iframe
                    src="https://www.youtube.com/embed/$videoId?autoplay=1&rel=0&playsinline=1"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowfullscreen>
                  </iframe>
                </div>
                </body>
                </html>
            """.trimIndent()

            webView.loadDataWithBaseURL(
                "https://www.youtube.com",
                embedHtml,
                "text/html",
                "utf-8",
                null
            )

        } else {
            // Direct video file (mp4, etc.) → use VideoView
            webView.visibility = View.GONE
            videoView.visibility = View.VISIBLE

            val mediaController = MediaController(this)
            mediaController.setAnchorView(videoView)
            videoView.setMediaController(mediaController)

            try {
                videoView.setVideoURI(Uri.parse(videoUrl))
                videoView.setOnPreparedListener { mp ->
                    progressBar.visibility = View.GONE
                    mp.start()
                }
                videoView.setOnErrorListener { _, _, _ ->
                    progressBar.visibility = View.GONE
                    videoView.visibility = View.GONE
                    noVideoLayout.visibility = View.VISIBLE
                    true
                }
                videoView.requestFocus()
                videoView.start()
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                videoView.visibility = View.GONE
                noVideoLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) videoView.pause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
        webView.destroy()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
