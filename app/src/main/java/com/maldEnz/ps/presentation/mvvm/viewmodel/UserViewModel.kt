package com.maldEnz.ps.presentation.mvvm.viewmodel

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    var name = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var imageUri = MutableLiveData<Uri?>()

    var passwordAuth = MutableLiveData<String>()

    init {
        passwordAuth.value = ""
    }

    fun updateProfileName(context: Context, newName: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            if (newName.isNotEmpty() || newName != "") {
                val documentReference =
                    FirebaseFirestore.getInstance().collection("Users").document(getCurrentUser())
                val updatedName = hashMapOf(
                    "userName" to newName,
                )

                documentReference.update(updatedName as Map<String, Any>).addOnSuccessListener {
                    Toast.makeText(context, "Name Updated", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun updateProfilePicture(context: Context) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Updating")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val imageName = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().reference.child("profileImages/$imageName")

        val uploadTask: UploadTask = imageRef.putFile(imageUri.value!!)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnCompleteListener { task ->
                val imageUri = task.result.toString()
                if (task.isSuccessful) {
                    val documentReference =
                        FirebaseFirestore.getInstance().collection("Users")
                            .document(getCurrentUser())
                    val updatedImage = hashMapOf(
                        "image" to imageUri,
                    )

                    documentReference.update(updatedImage as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Name Updated", Toast.LENGTH_LONG).show()
                        }
                    progressDialog.dismiss()
                }
            }
        }
    }

    fun getUserData(imageView: ImageView) {
        val documentReference = FirebaseFirestore.getInstance().collection("Users")
            .document(getCurrentUser())

        documentReference.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val fullName = snapshot.getString("userName")
                val mail = snapshot.getString("userEmail")
                val imageUrl = snapshot.getString("image")
                if (fullName != null && mail != null && imageUrl != null) {
                    name!!.value = fullName
                    email!!.value = mail
                    Glide.with(imageView.context)
                        .load(imageUrl)
                        .into(imageView)
                }
            }
        }
    }

    private fun getCurrentUser(): String {
        return auth.currentUser!!.uid
    }
}
