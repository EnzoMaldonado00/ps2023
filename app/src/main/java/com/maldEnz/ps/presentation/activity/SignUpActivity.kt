package com.maldEnz.ps.presentation.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.maldEnz.ps.databinding.ActivitySignUpBinding
import com.maldEnz.ps.presentation.fragment.ImageMethodFragment

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buttonListener()
        onBackHandler(this)
    }

    private fun buttonListener() {
        binding.userIcon.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.alreadyRegisteredText.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val fullName = binding.nameEditText.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(fullName, email, password)
            }
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
                        .into(binding.userIcon)
                }
            }
        }

    private fun registerUser(fullName: String, email: String, password: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Register in progress")
        progressDialog.setCancelable(false)
        progressDialog.show()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { registrationTask ->
                if (registrationTask.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val uid = user!!.uid

                    val imageName = "profileImages/$uid.jpg"
                    val imageRef = FirebaseStorage.getInstance().reference.child(imageName)
                    val uploadTask: UploadTask = imageRef.putFile(imageUri!!)

                    uploadTask.addOnSuccessListener {
                        imageRef.downloadUrl.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val imageUri = task.result.toString()
                                val hashMap = hashMapOf(
                                    "usedId" to uid,
                                    "userName" to fullName,
                                    "userEmail" to email,
                                    "status" to "default",
                                    "image" to imageUri,
                                    "password" to password,
                                )

                                FirebaseFirestore.getInstance().collection("Users")
                                    .document(uid)
                                    .set(hashMap)
                                    .addOnCompleteListener { firestoreTask ->
                                        if (firestoreTask.isSuccessful) {
                                            progressDialog.dismiss()
                                            startActivity(Intent(this, LogInActivity::class.java))
                                            finish()
                                        }
                                    }
                            }
                        }
                    }
                }
            }
    }

    // Arreglar para poder usar la camara
    private fun selectImageDialog() {
        val dialogFragment = ImageMethodFragment()
        dialogFragment.show(supportFragmentManager, "dialog")
    }

    private fun onBackHandler(context: Context) {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(context, LogInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }
}
