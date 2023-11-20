package com.maldEnz.ps.presentation.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun addTheme(themeName: String, description: String, price: Long) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val docRefer = firestore.collection("Themes")

                val hashMap = hashMapOf(
                    "themeName" to themeName,
                    "description" to description,
                    "price" to price,
                    "timesUnlocked" to 0L,
                )

                docRefer.add(hashMap)
            }
        }
}
