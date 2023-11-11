package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ActivityFriendProfileBinding
import com.maldEnz.ps.presentation.adapter.PostAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import org.koin.android.ext.android.inject

class FriendProfileActivity : AppCompatActivity() {

    private lateinit var friendUid: String
    private lateinit var adapter: PostAdapter
    private val friendViewModel: FriendViewModel by inject()

    private lateinit var binding: ActivityFriendProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        friendUid = intent.getStringExtra("friendUid") ?: ""
        friendViewModel.loadFriendData(friendUid)
        friendViewModel.friend.observe(this) {
            binding.name.text = it.userName
            binding.email.text = it.userEmail
            Glide.with(this)
                .load(it.userImage)
                .into(binding.profileImage)
        }
        adapter = PostAdapter()
        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.recycler.layoutManager = gridLayoutManager
        binding.recycler.adapter = adapter
        binding.recycler.itemAnimator = null
        friendViewModel.getFriendPost(friendUid)
        friendViewModel.friendPostList.observe(this) {
            adapter.submitList(it)
        }
    }
}
