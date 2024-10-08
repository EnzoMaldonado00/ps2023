package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ActivityProfileBinding
import com.maldEnz.ps.presentation.adapter.PostAdapter
import com.maldEnz.ps.presentation.fragment.dialog.SheetDialogProfileFragment
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var adapter: PostAdapter
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        observers()

        binding.profileName.setOnClickListener {
            SheetDialogProfileFragment().show(supportFragmentManager, "UpdateName")
        }

        binding.profilePicture.setOnClickListener {
            startActivity(Intent(this, ImagePickerActivity::class.java))
        }

        adapter = PostAdapter()
        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.postRecyclerView.layoutManager = gridLayoutManager
        binding.postRecyclerView.adapter = adapter
        binding.postRecyclerView.itemAnimator = null

        userViewModel.postList.observe(this) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.emptyStatePosts.visibility = View.VISIBLE
            } else {
                binding.emptyStatePosts.visibility = View.GONE
            }
        }
    }

    private fun observers() {
        userViewModel.name.observe(this) {
            binding.profileName.text = String.format("%s", it)
        }
        userViewModel.email.observe(this) {
            binding.profileMail.text = String.format("%s", it)
        }
        userViewModel.imageURL.observe(this) {
            Glide.with(this)
                .load(String.format("%s", it))
                .into(binding.profilePicture)
        }
    }

    override fun onResume() {
        super.onResume()
        userViewModel.getUserData()
        userViewModel.updateUserStatusToOnline()
        observers()
    }
}
