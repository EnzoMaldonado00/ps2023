package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maldEnz.ps.databinding.ActivityAddThemeBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.AdminViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class AddThemeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddThemeBinding
    private val adminViewModel: AdminViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)

        binding = ActivityAddThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val themeName = binding.themeName.text.toString()
            val themeDescription = binding.themeDescription.text.toString()
            val themePrice = binding.themePrice.text.toString()
            adminViewModel.addTheme(
                themeName,
                themeDescription,
                themePrice.toLong(),
            )
            finish()
            Toast.makeText(this, "Theme Added", Toast.LENGTH_SHORT).show()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
