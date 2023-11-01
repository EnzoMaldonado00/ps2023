package com.maldEnz.ps.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ActivityPostDetailsBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.ChatViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailsBinding
    private val userViewModel: UserViewModel by inject()
    private val chatViewModel: ChatViewModel by inject()

    private lateinit var auth: FirebaseAuth
    private lateinit var authorId: String
    private lateinit var imagePost: String
    private lateinit var dateTime: String
    private lateinit var description: String
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        authorId = intent.getStringExtra("authorId") ?: ""
        imagePost = intent.getStringExtra("imagePost") ?: ""
        dateTime = intent.getStringExtra("dateTime") ?: ""
        description = intent.getStringExtra("description") ?: ""
        postId = intent.getStringExtra("postId") ?: ""

        binding.datePosted.text = dateTime
        binding.description.text = description
        Glide.with(this)
            .load(imagePost)
            .into(binding.imagePost)

        chatViewModel.loadUserData(auth.currentUser!!.uid, authorId,binding.profileName,binding.profilePicture)


    }
}