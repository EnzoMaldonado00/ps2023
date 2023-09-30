package com.maldEnz.ps.presentation.mvvm.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.maldEnz.ps.presentation.mvvm.model.FriendModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserViewModel : ViewModel() {
    // HANDLE POSSIBLE EXCEPTIONS

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    var name = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var imageUri = MutableLiveData<Uri?>()
    var imageURL = MutableLiveData<String>()
    val friendList = MutableLiveData<List<Map<String, Any>>>()
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
                    firestore.collection("Users").document(getCurrentUser())
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
            storage.reference.child("profileImages/$currentUser.jpg")

        val uploadTask: UploadTask = imageRef.putFile(imageUri.value!!)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnCompleteListener { task ->
                val imageUri = task.result.toString()
                if (task.isSuccessful) {
                    val documentReference =
                        firestore.collection("Users")
                            .document(getCurrentUser())
                    val updatedImage = hashMapOf(
                        "image" to imageUri,
                    )

                    documentReference.update(updatedImage as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Profile Picture Updated", Toast.LENGTH_LONG)
                                .show()
                        }
                    // progressDialog.dismiss()
                }
            }
        }
    }

    fun getUserData() = viewModelScope.launch {
        val docRefer = firestore.collection("Users")
            .document(getCurrentUser())

        docRefer.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val fullName = snapshot.getString("userName")
                val mail = snapshot.getString("userEmail")
                val imageUrl = snapshot.getString("image")
                if (fullName != null && mail != null && imageUrl != null) {
                    name!!.value = fullName
                    email!!.value = mail
                    imageURL!!.value = imageUrl
                }
            }
        }
    }

    fun addFriend(friendEmail: String, context: Context) = viewModelScope.launch {
        val currentUser = getCurrentUser()
        val colRef = firestore.collection("Users")
        val docRef = colRef.document(currentUser)

        docRef.get().addOnSuccessListener { doc ->
            val currentUserData = doc.data
            val currentFriends = currentUserData!!["friends"] as? List<HashMap<String, Any>>
            friendList.value = currentFriends!!

            // Verify that the friend is already added
            val isAlreadyFriend = currentFriends?.any { friendData ->
                friendData["friendEmail"] == friendEmail
            } ?: false

            if (!isAlreadyFriend) {
                colRef.whereEqualTo("userEmail", friendEmail).get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            val friendId = document.id
                            if (friendId != currentUser) {
                                val friendName = document.getString("userName")!!
                                val image = document.getString("image")!!

                                val fr = FriendModel(friendId, friendName, friendEmail, image)

                                docRef.update("friends", FieldValue.arrayUnion(fr))
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Friend added", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "You cannot add yourself as a friend",
                                    Toast.LENGTH_SHORT,
                                )
                                    .show()
                            }
                        }
                    }
            } else {
                Toast.makeText(
                    context,
                    "You are already friends with this user",
                    Toast.LENGTH_SHORT,
                )
                    .show()
            }
        }
    }
}
