package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.maldEnz.ps.databinding.ActivityGameBinding
import com.maldEnz.ps.presentation.adapter.FriendHighScoreAdapter
import com.maldEnz.ps.presentation.gamecontroller.GameView
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var adapter: FriendHighScoreAdapter
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        userViewModel.getFriendsHighestScore()

        adapter = FriendHighScoreAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        userViewModel.scoreList.observe(this) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.emptyStateScore.visibility = View.VISIBLE
            } else {
                binding.emptyStateScore.visibility = View.GONE
            }
        }

        binding.btnStart.setOnClickListener {
            val gameView = GameView(this)
            setContentView(gameView)
        }
    }
}
