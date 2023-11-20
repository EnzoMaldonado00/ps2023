package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ActivityPostDetailsBinding
import com.maldEnz.ps.presentation.adapter.CommentAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.PostViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding
    private val postViewModel: PostViewModel by inject()
    private val friendViewModel: FriendViewModel by inject()

    private lateinit var auth: FirebaseAuth
    private lateinit var authorId: String
    private lateinit var imagePost: String
    private lateinit var dateTime: String
    private lateinit var description: String
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)

        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        authorId = intent.getStringExtra("authorId") ?: ""
        imagePost = intent.getStringExtra("imagePost") ?: ""
        dateTime = intent.getStringExtra("dateTime") ?: ""
        description = intent.getStringExtra("description") ?: ""
        postId = intent.getStringExtra("postId") ?: ""

        friendViewModel.loadFriendData(authorId)
        postViewModel.getPostDetailsComment(postId, authorId)

        binding.datePosted.text = dateTime
        binding.description.text = description
        Glide.with(this)
            .load(imagePost)
            .into(binding.imagePost)

        friendViewModel.friend.observe(this) {
            binding.profileName.text = it.userName
            Glide.with(this)
                .load(it.userImage)
                .into(binding.profilePicture)
        }

        val adapter = CommentAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.commentRecycler.layoutManager = layoutManager
        binding.commentRecycler.adapter = adapter
        binding.commentRecycler.itemAnimator = null

        postViewModel.postComments.observe(this) {
            adapter.submitList(it)
            binding.commentRecycler.scrollToPosition(it.lastIndex)
            binding.commentsCount.text = String.format("(%s)", it.size)
            if (it.isEmpty()) {
                binding.emptyStateComments.visibility = View.VISIBLE
            } else {
                binding.emptyStateComments.visibility = View.GONE
            }
        }

        binding.btnSend.setOnClickListener {
            val commentContent = binding.commentContent.text.toString()
            postViewModel.uploadPostComment(postId, authorId, commentContent)
            binding.commentContent.text.clear()
            binding.commentContent.clearFocus()
        }

        binding.btnLike.setOnClickListener {
            postViewModel.setPostLike(postId, authorId)
        }
    }
}
