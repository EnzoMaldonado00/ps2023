package com.maldEnz.ps.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.maldEnz.ps.databinding.FragmentImageMethodBinding

class ImageMethodFragment : DialogFragment() {

    private val pickImageRC = 1
    private val imageCaptureRC = 2
    private lateinit var imageUri: Uri
    private lateinit var imageView: ImageView

    private lateinit var binding: FragmentImageMethodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentImageMethodBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cameraImage.setOnClickListener {
            requestImageFromCamera()
            dismiss()
        }
        binding.galleryImage.setOnClickListener {
            requestImageFromGallery()
            dismiss()
        }
    }

    private fun requestImageFromGallery() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, pickImageRC)
    }

    private fun requestImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, imageCaptureRC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Selecciona una imagen desde la galería
            imageUri = data.data!!
            // Muestra la imagen en el ImageView
            imageView.setImageURI(imageUri)
        }
    }

    companion object {
        private const val ARGS_TITLE_KEY = "image"

        fun newInstance(imageView: ImageView): ImageMethodFragment {
            val fragment = ImageMethodFragment()
            val args = Bundle()
            args.putString(ARGS_TITLE_KEY, imageView.toString())
            fragment.arguments = args
            return fragment
        }
    }
}
