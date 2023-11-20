package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.maldEnz.ps.databinding.ActivityGameOverBinding
import com.maldEnz.ps.presentation.gamecontroller.GameView
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class GameOverActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameOverBinding
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)

        binding = ActivityGameOverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userViewModel.getHighestScore()
        val score = intent.getIntExtra("score", 0)
        userViewModel.highestScore.observe(this) {
            if (score > it.toInt()) {
                userViewModel.updateHighestScore(score.toString())
                binding.highestScore.text = score.toString()
                binding.newHighscore.visibility = View.VISIBLE
            } else {
                binding.highestScore.text = it
            }
        }

        if (score >= 500) {
            userViewModel.convertScoreToCoins(score)
        }

        binding.score.text = score.toString()

        binding.btnRestart.setOnClickListener {
            val gameView = GameView(this)
            setContentView(gameView)
        }
        binding.btnExit.setOnClickListener {
            finish()
        }
    }
}
