package com.maldEnz.ps.presentation.mvvm.viewmodel

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

class UserViewModel : ViewModel() {
    // HANDLE POSSIBLE EXCEPTIONS

    private val auth = FirebaseAuth.getInstance()
    var name = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var imageUri = MutableLiveData<Uri?>()

    var passwordAuth = MutableLiveData<String>()

    init {
        passwordAuth.value = ""
    }

    private fun getCurrentUser(): String {
        return auth.currentUser!!.uid
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

    fun updateProfilePicture(context: Context) = viewModelScope.launch {
        val currentUser = getCurrentUser()
        val imageRef =
            FirebaseStorage.getInstance().reference.child("profileImages/$currentUser.jpg")

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
                    // progressDialog.dismiss()
                }
            }
        }
    }

    fun getUserData(imageView: ImageView) = viewModelScope.launch {
        val docRefer = FirebaseFirestore.getInstance().collection("Users")
            .document(getCurrentUser())

        docRefer.addSnapshotListener { snapshot, _ ->
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

    fun addFriend() {
        val user = getCurrentUser()

        val firestore = FirebaseFirestore.getInstance()

    }
}
