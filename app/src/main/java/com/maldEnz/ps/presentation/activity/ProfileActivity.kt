package com.maldEnz.ps.presentation.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ActivityProfileBinding
import com.maldEnz.ps.presentation.fragment.SheetDialogProfileFragment
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private var imageUri: Uri? = null
    private val pickImageRC = 1

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        observers()

        binding.profileName.setOnClickListener {
            SheetDialogProfileFragment().show(supportFragmentManager, "UpdateName")
        }

        binding.profilePicture.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, pickImageRC)
        }

        userViewModel.getUserData(binding.profilePicture)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageRC && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Selecciona una imagen desde la galería
            imageUri = data.data
            // Muestra la imagen en el ImageView
            binding.profilePicture.setImageURI(imageUri)
        }
    }

    private fun observers() {
        userViewModel.name.observe(this) {
            binding.profileName.text = String.format("%s", it)
        }
        userViewModel.email.observe(this) {
            binding.profileMail.text = String.format("%s", it)
        }
        userViewModel.imageUrl.observe(this) {
            Glide.with(this)
                .load(String.format("%s", it))
                .into(binding.profilePicture)
        }
    }
}
