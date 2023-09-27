package com.maldEnz.ps.presentation.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.maldEnz.ps.databinding.ActivitySignUpBinding
import com.maldEnz.ps.presentation.fragment.ImageMethodFragment
import com.maldEnz.ps.presentation.fragment.SheetDialogProfileFragment
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var imageUri: Uri? = null
    private val pickImageRC = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buttonListener()
        onBackHandler(this)
    }

    private fun buttonListener() {
        binding.userIcon.setOnClickListener {
            ImageMethodFragment().show(supportFragmentManager, "SelectImage")

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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == pickImageRC && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Selecciona una imagen desde la galería
            imageUri = data.data
            // Muestra la imagen en el ImageView
            binding.userIcon.setImageURI(imageUri)
        }
    }

    private fun registerUser(fullName: String, email: String, password: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Register in progress")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val imageName = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().reference.child("profileImages/$imageName")

        val uploadTask: UploadTask = imageRef.putFile(imageUri!!)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val imageUri = task.result.toString()
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val user = FirebaseAuth.getInstance().currentUser
                                val hashMap = hashMapOf(
                                    "usedId" to user!!.uid,
                                    "userName" to fullName,
                                    "userEmail" to email,
                                    "status" to "default",
                                    "image" to imageUri,
                                )

                                FirebaseFirestore.getInstance().collection("Users")
                                    .document(user.uid)
                                    .set(hashMap)
                                progressDialog.dismiss()
                                startActivity(Intent(this, LogInActivity::class.java))
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
