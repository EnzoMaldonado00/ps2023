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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    var name = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var imageUrl = MutableLiveData<String>()
    var imageUri = MutableLiveData<Uri>()

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


    fun updateProfilePicture(){

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
