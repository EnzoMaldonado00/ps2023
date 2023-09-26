package com.maldEnz.ps.presentation.mvvm.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private var currentUser: FirebaseUser? = auth.currentUser

    fun getCurrentUser(): FirebaseUser? {
        return currentUser
    }

    fun signOut() {
        auth.signOut()
        currentUser = null
    }
}
