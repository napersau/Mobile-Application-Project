package com.example.fe.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fe.R
import com.example.fe.model.Course
import com.example.fe.ui.adapter.CourseAdapter
import com.example.fe.viewmodel.CourseViewModel

class CoursesListActivity : AppCompatActivity() {

    private lateinit var viewModel: CourseViewModel
    private lateinit var adapter: CourseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_courses_list)

        setupToolbar()
        setupViews()
        setupRecyclerView()
        setupViewModel()
        loadCourses()
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            title = "Khóa học"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerViewCourses)
        progressBar = findViewById(R.id.progressBar)
        emptyLayout = findViewById(R.id.emptyLayout)
    }

    private fun setupRecyclerView() {
        adapter = CourseAdapter { course ->
            openCourseDetail(course)
        }

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@CoursesListActivity, 2)
            adapter = this@CoursesListActivity.adapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]

        // Observe courses list
        viewModel.coursesLiveData.observe(this) { result ->
            result.onSuccess { courses ->
                showCourses(courses)
            }.onFailure { error ->
                showError(error.message ?: "Không thể tải danh sách khóa học")
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun loadCourses() {
        viewModel.getAllCourses()
    }

    private fun showCourses(courses: List<Course>) {
        if (courses.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
            adapter.submitList(courses)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        recyclerView.visibility = View.GONE
        emptyLayout.visibility = View.VISIBLE
    }

    private fun openCourseDetail(course: Course) {
        val intent = Intent(this, CourseDetailActivity::class.java).apply {
            putExtra("COURSE_ID", course.id)
            putExtra("COURSE_TITLE", course.title)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when returning
        loadCourses()
    }
}

