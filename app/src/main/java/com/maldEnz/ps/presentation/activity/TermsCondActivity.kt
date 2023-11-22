package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maldEnz.ps.databinding.ActivityTermsCondBinding

class TermsCondActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsCondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsCondBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
