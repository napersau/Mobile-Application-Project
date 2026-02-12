package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fe.R
import com.example.fe.model.Course
import com.example.fe.model.Lesson
import com.example.fe.model.LessonType
import com.example.fe.ui.adapter.SectionAdapter
import com.example.fe.viewmodel.CourseViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.button.MaterialButton

class CourseDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: CourseViewModel
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var ivCourseHeaderImage: ImageView
    private lateinit var tvLevel: TextView
    private lateinit var tvStudents: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvPrice: TextView
    private lateinit var recyclerViewSections: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptySections: TextView
    private lateinit var btnEnroll: MaterialButton

    private var courseId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        courseId = intent.getLongExtra("COURSE_ID", -1)

        setupToolbar()
        setupViews()
        setupViewModel()

        if (courseId != -1L) {
            loadCourseDetail()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupViews() {
        collapsingToolbar = findViewById(R.id.collapsingToolbar)
        ivCourseHeaderImage = findViewById(R.id.ivCourseHeaderImage)
        tvLevel = findViewById(R.id.tvLevel)
        tvStudents = findViewById(R.id.tvStudents)
        tvDescription = findViewById(R.id.tvDescription)
        tvPrice = findViewById(R.id.tvPrice)
        recyclerViewSections = findViewById(R.id.recyclerViewSections)
        progressBar = findViewById(R.id.progressBar)
        tvEmptySections = findViewById(R.id.tvEmptySections)
        btnEnroll = findViewById(R.id.btnEnroll)

        recyclerViewSections.layoutManager = LinearLayoutManager(this)

        btnEnroll.setOnClickListener {
            enrollCourse()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]

        viewModel.courseDetailLiveData.observe(this) { result ->
            result.onSuccess { course ->
                displayCourseDetail(course)
            }.onFailure { error ->
                showError(error.message ?: "Không thể tải thông tin khóa học")
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun loadCourseDetail() {
        viewModel.getCourseById(courseId)
    }

    private fun displayCourseDetail(course: Course) {
        collapsingToolbar.title = course.title

        if (!course.imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(course.imageUrl)
                .placeholder(R.drawable.ic_course)
                .error(R.drawable.ic_course)
                .centerCrop()
                .into(ivCourseHeaderImage)
        }

        tvLevel.text = course.level ?: "All Levels"
        tvStudents.text = "👥 ${course.totalStudents} học viên"
        tvDescription.text = course.description ?: "Chưa có mô tả"
        tvPrice.text = when {
            course.price == null || course.price == 0.0 -> "Miễn phí"
            else -> "${formatPrice(course.price)} VNĐ"
        }

        if (course.sections.isNullOrEmpty()) {
            tvEmptySections.visibility = View.VISIBLE
            recyclerViewSections.visibility = View.GONE
        } else {
            tvEmptySections.visibility = View.GONE
            recyclerViewSections.visibility = View.VISIBLE

            val sectionAdapter = SectionAdapter(course.sections) { lesson ->
                openLesson(lesson)
            }
            recyclerViewSections.adapter = sectionAdapter
        }
    }

    private fun openLesson(lesson: Lesson) {
        when (lesson.type) {
            LessonType.VIDEO -> {
                Toast.makeText(this, "Video: ${lesson.title}", Toast.LENGTH_SHORT).show()
            }
            LessonType.DOCUMENT -> {
                if (lesson.document != null) {
                    val intent = Intent(this, DocumentDetailActivity::class.java).apply {
                        putExtra("DOCUMENT_ID", lesson.document.id)
                    }
                    startActivity(intent)
                }
            }
            LessonType.FLASHCARD -> {
                if (lesson.deck != null) {
                    val intent = Intent(this, DeckDetailActivity::class.java).apply {
                        putExtra("DECK_ID", lesson.deck.id)
                        putExtra("DECK_NAME", lesson.deck.title)
                    }
                    startActivity(intent)
                }
            }
            LessonType.EXAM -> {
                Toast.makeText(this, "Exam: ${lesson.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enrollCourse() {
        Toast.makeText(this, "Tính năng đăng ký khóa học đang được phát triển", Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatPrice(price: Double): String {
        return String.format("%,.0f", price)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

