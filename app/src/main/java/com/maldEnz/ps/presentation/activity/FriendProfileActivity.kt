package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ActivityFriendProfileBinding
import com.maldEnz.ps.presentation.adapter.UserPostAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import org.koin.android.ext.android.inject

class FriendProfileActivity : AppCompatActivity() {

    private lateinit var friendUid: String
    private lateinit var friendName: String
    private lateinit var friendImage: String
    private lateinit var friendEmail: String
    private lateinit var adapter: UserPostAdapter
    private val friendViewModel: FriendViewModel by inject()

    private lateinit var binding: ActivityFriendProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        friendUid = intent.getStringExtra("friendUid") ?: ""
        friendName = intent.getStringExtra("friendName") ?: ""
        friendImage = intent.getStringExtra("friendImageProfile") ?: ""
        friendEmail = intent.getStringExtra("friendEmail") ?: ""

        binding.name.text = friendName
        binding.email.text = friendEmail
        Glide.with(this)
            .load(friendImage)
            .into(binding.profileImage)

        adapter = UserPostAdapter()
        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter
        friendViewModel.getFriendPost(friendUid)
        friendViewModel.friendPostList.observe(this) {
            adapter.submitList(it)
        }
    }
}
