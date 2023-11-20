package com.maldEnz.ps.presentation.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ActivityUploadPostBinding
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils
import org.koin.android.ext.android.inject

class UploadPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadPostBinding
    private val userViewModel: UserViewModel by inject()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FunUtils.setAppTheme(this)

        binding = ActivityUploadPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.getUserData()

        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)

        binding.btnPost.setOnClickListener {
            if (imageUri != null) {
                userViewModel.uploadPost(binding.descText.text.toString(), imageUri!!)
                Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    imageUri = data.data
                    Glide.with(this)
                        .load(imageUri)
                        .into(binding.image)
                }
            } else {
                finish()
            }
        }
}
