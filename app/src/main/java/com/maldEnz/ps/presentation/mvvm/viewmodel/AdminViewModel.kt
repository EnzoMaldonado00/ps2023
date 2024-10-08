package com.maldEnz.ps.presentation.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maldEnz.ps.presentation.util.FunUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!.uid
    val registeredUsers = MutableLiveData<String>()
    val dateRegisterUsers = MutableLiveData<Map<String, Int>>()

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

    fun getAllUsersRegisterDate() {
        val usersCollection = firestore.collection("Users")

        usersCollection.get()
            .addOnSuccessListener { documents ->
                val usersPerDateMap = mutableMapOf<String, Int>()

                for (document in documents) {
                    val documentId = document.id
                    val userRef = firestore.collection("Users").document(documentId)
                    userRef.get()
                        .addOnSuccessListener { userDocument ->
                            if (userDocument != null && userDocument.exists()) {
                                val registerDate = FunUtils.formatDateTime(userDocument.getString("registerDate")!!)

                                registerDate.let {
                                    if (usersPerDateMap.containsKey(it)) {
                                        val count = usersPerDateMap[it] ?: 0
                                        usersPerDateMap[it] = count + 1
                                    } else {
                                        usersPerDateMap[it] = 1
                                    }
                                }
                            }
                            dateRegisterUsers.value = usersPerDateMap
                        }
                }
            }
    }
}
