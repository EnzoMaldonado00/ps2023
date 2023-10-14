package com.maldEnz.ps.presentation.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.maldEnz.ps.databinding.ActivityImagePickerBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class ImagePickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImagePickerBinding
    private var selectedImageUri: Uri? = null
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openImagePicker()
        userViewModel.imageUri.observe(this) {
            binding.imageSelected.setImageURI(userViewModel.imageUri.value)
        }

        binding.btnConfirm.setOnClickListener {
            userViewModel.updateProfilePicture(this)
            finish()
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    selectedImageUri = data.data
                    userViewModel.imageUri.value = selectedImageUri
                    binding.imageSelected.setImageURI(selectedImageUri)
                }
            } else {
                finish()
            }
        }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }
}
