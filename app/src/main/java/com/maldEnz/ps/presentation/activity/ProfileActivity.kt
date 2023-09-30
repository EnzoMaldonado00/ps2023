package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ActivityProfileBinding
import com.maldEnz.ps.presentation.fragment.SheetDialogProfileFragment
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        userViewModel.getUserData()
        observers()

        binding.profileName.setOnClickListener {
            SheetDialogProfileFragment().show(supportFragmentManager, "UpdateName")
        }

        binding.profilePicture.setOnClickListener {
            startActivity(Intent(this, ImagePickerActivity::class.java))
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
        observers()
    }
}
