package com.maldEnz.ps.presentation.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
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
    private var imageUri: Uri? = null
    private val pickImageRC = 1

    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        userViewModel.getUserData(binding.profilePicture)
        observers()
        Log.d("ImagePickerActivity", "onResume ")

        binding.profileName.setOnClickListener {
            SheetDialogProfileFragment().show(supportFragmentManager, "UpdateName")
        }

        binding.profilePicture.setOnClickListener {
            startActivity(Intent(this, ImagePickerActivity::class.java))
        }
    }

    // otra manera de obtener la imagen sin usar el metodo deprecado
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    imageUri = data.data
                    Glide.with(this)
                        .load(imageUri)
                        .into(binding.profilePicture)
                }
            }
        }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageRC && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Selecciona una imagen desde la galería
            imageUri = data.data
            // Muestra la imagen en el ImageView
            Glide.with(this)
                .load(imageUri)
                .into(binding.profilePicture)
        }
    }

    private fun observers() {
        userViewModel.name.observe(this) {
            binding.profileName.text = String.format("%s", it)
        }
        userViewModel.email.observe(this) {
            binding.profileMail.text = String.format("%s", it)
        }
        userViewModel.imageUri.observe(this) {
            Glide.with(this)
                .load(String.format("%s", it))
                .into(binding.profilePicture)
        }
    }

    override fun onResume() {
        super.onResume()

        userViewModel.getUserData(binding.profilePicture)
        observers()
    }
}
