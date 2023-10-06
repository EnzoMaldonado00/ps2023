package com.maldEnz.ps.presentation.mvvm.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PostViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun uploadPost(){

    }
}
