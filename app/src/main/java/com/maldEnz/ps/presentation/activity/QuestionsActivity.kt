package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maldEnz.ps.databinding.ActivityQuestionsBinding

class QuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
