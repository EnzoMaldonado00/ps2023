package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.maldEnz.ps.databinding.ActivityShopBinding
import com.maldEnz.ps.presentation.adapter.ShopAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class ShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopBinding
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel.getThemes()
        userViewModel.getUserData()
        val adapter = ShopAdapter(userViewModel)
        binding.recycler.layoutManager = GridLayoutManager(this, 2)
        binding.recycler.adapter = adapter
        userViewModel.themesList.observe(this) {
            adapter.submitList(it)
        }
        userViewModel.coins.observe(this) {
            binding.coins.text = it.toString()
        }
    }
}
