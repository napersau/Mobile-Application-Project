package com.example.fe.ui.activity

import android.os.Bundle
import androidx.core.graphics.toColorInt
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.example.fe.R
import com.example.fe.model.DailyStudyStatResponse
import com.example.fe.viewmodel.AnalyticsViewModel
import com.example.fe.viewmodel.ProfileViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.chip.ChipGroup

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var analyticsViewModel: AnalyticsViewModel

    // User info views
    private lateinit var toolbar: Toolbar
    private lateinit var tvFullName: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvUserId: TextView
    private lateinit var progressBar: ProgressBar

    // Streak views
    private lateinit var tvCurrentStreak: TextView
    private lateinit var tvLongestStreak: TextView
    private lateinit var tvLearnedToday: TextView
    private lateinit var progressStreak: ProgressBar

    // Chart views
    private lateinit var barChart: BarChart
    private lateinit var tvChartEmpty: TextView
    private lateinit var progressChart: ProgressBar
    private lateinit var chipGroupDays: ChipGroup

    private var currentDays = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        setupToolbar()
        setupViewModels()
        observeViewModels()
        setupChipGroup()

        profileViewModel.getMyInfo()
        analyticsViewModel.loadGamification()
        analyticsViewModel.loadStudyStats(currentDays)
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        tvFullName = findViewById(R.id.tvFullName)
        tvUsername = findViewById(R.id.tvUsername)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvUserId = findViewById(R.id.tvUserId)
        progressBar = findViewById(R.id.progressBar)

        tvCurrentStreak = findViewById(R.id.tvCurrentStreak)
        tvLongestStreak = findViewById(R.id.tvLongestStreak)
        tvLearnedToday = findViewById(R.id.tvLearnedToday)
        progressStreak = findViewById(R.id.progressStreak)

        barChart = findViewById(R.id.barChart)
        tvChartEmpty = findViewById(R.id.tvChartEmpty)
        progressChart = findViewById(R.id.progressChart)
        chipGroupDays = findViewById(R.id.chipGroupDays)

        setupBarChart()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Hồ sơ của tôi"
        }
    }

    private fun setupViewModels() {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        analyticsViewModel = ViewModelProvider(this)[AnalyticsViewModel::class.java]
    }

    private fun setupChipGroup() {
        chipGroupDays.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            currentDays = when (checkedIds.first()) {
                R.id.chip7Days -> 7
                R.id.chip14Days -> 14
                R.id.chip30Days -> 30
                else -> 7
            }
            analyticsViewModel.loadStudyStats(currentDays)
        }
    }

    private fun setupBarChart() {
        barChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setFitBars(true)
            setPinchZoom(false)
            isDoubleTapToZoomEnabled = false
            setScaleEnabled(false)
            setNoDataText("Đang tải dữ liệu...")

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = "#757575".toColorInt()
                textSize = 10f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = "#E0E0E0".toColorInt()
                textColor = "#757575".toColorInt()
                axisMinimum = 0f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when {
                            value >= 3600 -> "${(value / 3600).toInt()}h"
                            value >= 60 -> "${(value / 60).toInt()}m"
                            else -> "${value.toInt()}s"
                        }
                    }
                }
            }

            axisRight.isEnabled = false
        }
    }

    private fun observeViewModels() {
        // Profile info
        profileViewModel.userInfo.observe(this) { result ->
            result.onSuccess { user ->
                tvFullName.text = user.fullName
                tvUsername.text = getString(R.string.username_format, user.username)
                tvEmail.text = user.email
                tvPhone.text = user.phoneNumber
                tvUserId.text = user.id.toString()
            }.onFailure { error ->
                Toast.makeText(this, error.message ?: "Không thể tải thông tin người dùng", Toast.LENGTH_LONG).show()
            }
        }

        profileViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Gamification / Streak
        analyticsViewModel.gamification.observe(this) { result ->
            result.onSuccess { data ->
                tvCurrentStreak.text = data.currentStreak.toString()
                tvLongestStreak.text = data.longestStreak.toString()
                tvLearnedToday.visibility = if (data.learnedToday) View.VISIBLE else View.GONE
            }.onFailure {
                tvCurrentStreak.text = "-"
                tvLongestStreak.text = "-"
            }
        }

        analyticsViewModel.isLoadingGamification.observe(this) { isLoading ->
            progressStreak.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Study stats chart
        analyticsViewModel.studyStats.observe(this) { result ->
            result.onSuccess { stats ->
                if (stats.isEmpty()) {
                    barChart.visibility = View.GONE
                    tvChartEmpty.visibility = View.VISIBLE
                } else {
                    barChart.visibility = View.VISIBLE
                    tvChartEmpty.visibility = View.GONE
                    renderBarChart(stats)
                }
            }.onFailure {
                barChart.visibility = View.GONE
                tvChartEmpty.visibility = View.VISIBLE
                tvChartEmpty.text = getString(R.string.chart_load_error)
            }
        }

        analyticsViewModel.isLoadingStats.observe(this) { isLoading ->
            progressChart.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) barChart.visibility = View.GONE
        }
    }

    private fun renderBarChart(stats: List<DailyStudyStatResponse>) {
        val entries = stats.mapIndexed { index, stat ->
            BarEntry(index.toFloat(), stat.durationSeconds.toFloat())
        }

        val labels = stats.map { stat ->
            // stat.date is "yyyy-MM-dd", show "dd/MM"
            try {
                val parts = stat.date.split("-")
                "${parts[2]}/${parts[1]}"
            } catch (_: Exception) {
                stat.date
            }
        }

        val dataSet = BarDataSet(entries, "Thời gian học (giây)").apply {
            val colors = stats.map { stat ->
                if (stat.hasLearned) "#4A90E2".toColorInt() else "#E0E0E0".toColorInt()
            }
            setColors(colors)
            setDrawValues(true)
            valueTextSize = 9f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if (value == 0f) return ""
                    return when {
                        value >= 3600 -> String.format(java.util.Locale.US, "%.1fh", value / 3600)
                        value >= 60 -> "${(value / 60).toInt()}m"
                        else -> "${value.toInt()}s"
                    }
                }
            }
        }

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.labelCount = labels.size.coerceAtMost(10)

        barChart.data = BarData(dataSet).apply {
            barWidth = 0.6f
        }
        barChart.animateY(600)
        barChart.invalidate()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
