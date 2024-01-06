package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.maldEnz.ps.databinding.ActivityAdminBinding
import com.maldEnz.ps.presentation.mvvm.model.ThemeModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.AdminViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Locale

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val adminViewModel: AdminViewModel by inject()
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adminViewModel.getRegisteredUsers()
        adminViewModel.getAllUsersRegisterDate()
        userViewModel.getThemes()
        //     userViewModel.getFeed()
        adminViewModel.registeredUsers.observe(this) {
            binding.registeredUsers.text = it
        }

        binding.btnAddTheme.setOnClickListener {
            startActivity(Intent(this, AddThemeActivity::class.java))
        }

        userViewModel.themesList.observe(this) {
            loadPieChart(it)
        }

        adminViewModel.dateRegisterUsers.observe(this) {
            loadBarChart(it)
        }
    }

    private fun loadPieChart(list: List<ThemeModel>) {
        val pieEntries = mutableListOf<PieEntry>()

        for (theme in list) {
            val timesUnlocked = theme.timesUnlocked.toFloat()
            pieEntries.add(PieEntry(timesUnlocked, theme.themeName))
        }

        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        pieDataSet.valueTextSize = 25f
        pieDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val pieData = PieData(pieDataSet)

        binding.apply {
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.animateY(1400, Easing.EaseInOutQuad)
            pieChart.invalidate()
        }
    }

    private fun loadBarChart(usersPerDateMap: Map<String, Int>) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val sortedEntries = usersPerDateMap.entries.sortedByDescending {
            dateFormat.parse(it.key)
        }
        val entries: ArrayList<BarEntry> = ArrayList()
        val labels: ArrayList<String> = ArrayList()

        var index = 0
        for ((date, count) in sortedEntries) {
            labels.add(date)
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
            index++
        }

        val barDataSet = BarDataSet(entries, "Registered Users")
        barDataSet.color = Color.rgb(34, 139, 34)
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
        val data = BarData(barDataSet)

        binding.apply {
            val yAxisFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
            barChart.axisLeft.valueFormatter = yAxisFormatter
            barChart.axisRight.valueFormatter = yAxisFormatter
            barDataSet.valueTextSize = 25f
            barChart.data = data
            barChart.xAxis.setDrawGridLines(false)
            barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barChart.setFitBars(true)
            barChart.description.isEnabled = false
            barChart.xAxis.granularity = 1f
            barChart.setVisibleXRangeMaximum(4f)
            barChart.invalidate()
        }
    }
}
