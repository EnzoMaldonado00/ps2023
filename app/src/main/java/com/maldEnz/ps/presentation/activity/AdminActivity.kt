package com.maldEnz.ps.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ActivityAdminBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.AdminViewModel
import org.koin.android.ext.android.inject

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val adminViewModel: AdminViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adminViewModel.getRegisteredUsers()
        adminViewModel.registeredUsers.observe(this){
            binding.registeredUsers.text = it
        }
    }
}