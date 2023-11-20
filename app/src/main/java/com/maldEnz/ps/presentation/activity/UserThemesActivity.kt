package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.maldEnz.ps.databinding.ActivityUserThemesBinding
import com.maldEnz.ps.presentation.adapter.UserThemesAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class UserThemesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserThemesBinding
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)
        binding = ActivityUserThemesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel.getUserThemes()

        val adapter = UserThemesAdapter(userViewModel, this)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter
        userViewModel.unlockedThemes.observe(this) {
            adapter.submitList(it)
        }
    }
}
