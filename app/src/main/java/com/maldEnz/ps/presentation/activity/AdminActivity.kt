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
import com.github.mikephil.charting.utils.ColorTemplate
import com.maldEnz.ps.databinding.ActivityAdminBinding
import com.maldEnz.ps.presentation.mvvm.model.FeedModel
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
        userViewModel.getThemes()
        userViewModel.getFeed()
        adminViewModel.registeredUsers.observe(this) {
            binding.registeredUsers.text = it
        }

        binding.btnAddTheme.setOnClickListener {
            startActivity(Intent(this, AddThemeActivity::class.java))
        }

        userViewModel.themesList.observe(this) {
            loadPieChart(it)
        }

        userViewModel.feedPostList.observe(this) {
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

        val pieData = PieData(pieDataSet)

        val pieChart = binding.pieChart
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.invalidate()
    }

    private fun loadBarChart(feedList: List<FeedModel>) {
        val postsByDate = mutableMapOf<String, Int>()

        for (feed in feedList) {
            val date = feed.postModel.dateTime
            val dateFormat = SimpleDateFormat("hh:mm dd-MM-yyyy", Locale.getDefault())

            val parsedDate = dateFormat.parse(date)
            val formattedDate =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(parsedDate)

            if (postsByDate.containsKey(formattedDate)) {
                val count = postsByDate[formattedDate] ?: 0
                postsByDate[formattedDate] = count + 1
            } else {
                postsByDate[formattedDate] = 1
            }
        }

        val entries = mutableListOf<BarEntry>()
        var index = 0f
        for ((date, count) in postsByDate) {
            val dateMillis =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(date)?.time?.toFloat()
                    ?: 0f
            entries.add(BarEntry(dateMillis, count.toFloat()))
            index++
        }

        val barDataSet = BarDataSet(entries, "Cantidad de Posts por Fecha")

        barDataSet.valueTextSize = 25f
        val barData = BarData(barDataSet)
        val barChart = binding.barChart

        barChart.data = barData
        barChart.description.isEnabled = false

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChart.axisRight.isEnabled = false
        val yAxis = barChart.axisLeft
        yAxis.setDrawGridLines(false)

        barChart.invalidate()
    }
}
