package com.maldEnz.ps.presentation.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!.uid
    val registeredUsers = MutableLiveData<String>()

    fun getRegisteredUsers() {
        val docRefer = firestore.collection("Users")

        docRefer.get().addOnSuccessListener {
            val totalUsers = it.size()
            registeredUsers.value = totalUsers.toString()
        }
    }
}
