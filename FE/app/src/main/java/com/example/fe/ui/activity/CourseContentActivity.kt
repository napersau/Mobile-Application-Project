package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.Lesson
import com.example.fe.model.LessonType
import com.example.fe.ui.adapter.SectionAdapter
import com.example.fe.viewmodel.CourseContentViewModel

class CourseContentActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COURSE_ID = "COURSE_ID"
        const val EXTRA_COURSE_TITLE = "COURSE_TITLE"
    }

    private lateinit var viewModel: CourseContentViewModel
    private lateinit var recyclerViewSections: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCourseTitle: TextView
    private lateinit var tvCourseLevel: TextView
    private lateinit var tvTotalLessons: TextView
    private lateinit var progressCourse: ProgressBar
    private lateinit var tvProgressPercent: TextView
    private lateinit var emptyLayout: View

    private var courseId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_content)

        courseId = intent.getLongExtra(EXTRA_COURSE_ID, -1L)
        val courseTitle = intent.getStringExtra(EXTRA_COURSE_TITLE) ?: "Khóa học"

        setupToolbar(courseTitle)
        setupViews()
        setupViewModel()

        if (courseId != -1L) {
            viewModel.loadCourse(courseId)
        } else {
            Toast.makeText(this, "Không tìm thấy khóa học", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupToolbar(title: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(title)
        }
    }

    private fun setupViews() {
        tvCourseTitle = findViewById(R.id.tvCourseTitle)
        tvCourseLevel = findViewById(R.id.tvCourseLevel)
        tvTotalLessons = findViewById(R.id.tvTotalLessons)
        progressCourse = findViewById(R.id.progressCourse)
        tvProgressPercent = findViewById(R.id.tvProgressPercent)
        recyclerViewSections = findViewById(R.id.recyclerViewSections)
        progressBar = findViewById(R.id.progressBar)
        emptyLayout = findViewById(R.id.emptyLayout)

        recyclerViewSections.layoutManager = LinearLayoutManager(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CourseContentViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.courseDetail.observe(this) { result ->
            result.onSuccess { course ->
                tvCourseTitle.text = course.title
                tvCourseLevel.text = course.level ?: "All Levels"
                supportActionBar?.title = course.title

                val sections = course.sections ?: emptyList()
                val totalLessons = sections.sumOf { it.lessons?.size ?: 0 }
                tvTotalLessons.text = "$totalLessons bài học"

                progressCourse.progress = 0
                tvProgressPercent.text = "0%"

                if (sections.isEmpty()) {
                    recyclerViewSections.visibility = View.GONE
                    emptyLayout.visibility = View.VISIBLE
                } else {
                    recyclerViewSections.visibility = View.VISIBLE
                    emptyLayout.visibility = View.GONE
                    recyclerViewSections.adapter = SectionAdapter(sections, expandAll = true) { lesson ->
                        openLesson(lesson)
                    }
                }
            }.onFailure { error ->
                Toast.makeText(
                    this,
                    error.message ?: "Không thể tải nội dung khóa học",
                    Toast.LENGTH_SHORT
                ).show()
                emptyLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun openLesson(lesson: Lesson) {
        when (lesson.type) {
            LessonType.VIDEO -> {
                val url = lesson.videoUrl
                if (!url.isNullOrEmpty()) {
                    // VideoPlayerActivity handles both YouTube (WebView embed) and direct video (VideoView)
                    val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                        putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, url)
                        putExtra(VideoPlayerActivity.EXTRA_LESSON_TITLE, lesson.title)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Video đang được cập nhật", Toast.LENGTH_SHORT).show()
                }
            }
            LessonType.DOCUMENT -> {
                if (lesson.document != null) {
                    val intent = Intent(this, DocumentDetailActivity::class.java).apply {
                        putExtra("DOCUMENT_ID", lesson.document.id)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Tài liệu đang được cập nhật", Toast.LENGTH_SHORT).show()
                }
            }
            LessonType.FLASHCARD -> {
                if (lesson.deck != null) {
                    val intent = Intent(this, DeckDetailActivity::class.java).apply {
                        putExtra("DECK_ID", lesson.deck.id)
                        putExtra("DECK_NAME", lesson.deck.title)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Bộ flashcard đang được cập nhật", Toast.LENGTH_SHORT).show()
                }
            }
            LessonType.EXAM -> {
                if (lesson.exam != null) {
                    val intent = Intent(this, ExamDetailActivity::class.java).apply {
                        putExtra("EXAM_ID", lesson.exam.id)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Bài kiểm tra đang được cập nhật", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
