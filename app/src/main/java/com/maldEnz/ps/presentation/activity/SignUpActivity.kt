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
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ActivitySignUpBinding
import com.maldEnz.ps.presentation.fragment.dialog.ErrorDialogFragment
import com.maldEnz.ps.presentation.fragment.dialog.ImageMethodFragment
import com.maldEnz.ps.presentation.util.FunUtils
import java.util.TimeZone

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buttonListener()
        onBackHandler(this)
        imageUri =
            Uri.parse("android.resource://com.maldEnz.ps/drawable/ic_default_acc")
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
            } else {
                val dialogFragment =
                    ErrorDialogFragment.newInstance(getString(R.string.field_error_dialog))
                dialogFragment.show(supportFragmentManager, "dialog")
            }
        }

        binding.termsAndCond.setOnClickListener {
            startActivity(Intent(this, TermsCondActivity::class.java))
        }
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    imageUri = data.data!!
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
                                    "userId" to uid,
                                    "userName" to fullName,
                                    "userEmail" to email,
                                    "status" to "default",
                                    "statusTimeZone" to TimeZone.getDefault().id.toString(),
                                    "image" to imageUri,
                                    "password" to password,
                                    "friends" to emptyList<Map<String, Any>>(),
                                    "friendRequests" to emptyList<Map<String, Any>>(),
                                    "posts" to emptyList<Map<String, Any>>(),
                                    "isAdmin" to false,
                                    "highestScore" to "0",
                                    "chats" to emptyList<Map<String, Any>>(),
                                    "coins" to 0,
                                    "themesUnlocked" to listOf(
                                        hashMapOf(
                                            "description" to "Default",
                                            "price" to 0,
                                            "themeName" to "DefaultTheme",
                                        ),
                                    ),
                                    "registerDate" to FunUtils.getDateTime(),
                                )

                                FirebaseFirestore.getInstance().collection("Users")
                                    .document(uid)
                                    .set(hashMap)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            progressDialog.dismiss()
                                            startActivity(Intent(this, LogInActivity::class.java))
                                            finish()
                                        }
                                    }
                            }
                        }
                    }
                }
            }.addOnFailureListener {
                progressDialog.dismiss()
                val dialogFragment =
                    ErrorDialogFragment.newInstance(getString(R.string.email_exists_dialog))
                dialogFragment.show(supportFragmentManager, "dialog")
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
