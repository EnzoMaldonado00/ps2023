package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maldEnz.ps.databinding.ActivityAdminBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.AdminViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val adminViewModel: AdminViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adminViewModel.getRegisteredUsers()
        adminViewModel.registeredUsers.observe(this) {
            binding.registeredUsers.text = it
        }

        binding.btnAddTheme.setOnClickListener {
            startActivity(Intent(this, AddThemeActivity::class.java))
        }
    }
}
