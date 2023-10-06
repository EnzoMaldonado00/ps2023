package com.maldEnz.ps.presentation.mvvm.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
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
import com.maldEnz.ps.presentation.mvvm.model.MessageModel
import com.maldEnz.ps.presentation.mvvm.model.UserModel
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
    val friends = MutableLiveData<List<FriendModel>>()
    val friendRequest = MutableLiveData<List<FriendModel>>()
    var passwordAuth = MutableLiveData<String>()
    var messageList = MutableLiveData<List<MessageModel>>()

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
        withContext(Dispatchers.IO) {
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
                                Toast.makeText(
                                    context,
                                    "Profile Picture Updated",
                                    Toast.LENGTH_LONG,
                                )
                                    .show()
                            }
                        // progressDialog.dismiss()
                    }
                }
            }
        }
    }

    fun getUserData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users")
                .document(getCurrentUser())

            docRefer.addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val fullName = snapshot.getString("userName")
                    val mail = snapshot.getString("userEmail")
                    val imageUrl = snapshot.getString("image")
                    val friendReqData = snapshot.get("friendRequests") as? List<Map<String, Any>>
                    val friendReq = friendReqData?.map { friendData ->
                        FriendModel(
                            friendId = friendData["userId"] as String,
                            friendName = friendData["userName"] as String,
                            friendEmail = friendData["userEmail"] as String,
                            friendImage = friendData["userImage"] as String,
                        )
                    } ?: emptyList()

                    val friendListData = snapshot.get("friends") as? List<Map<String, Any>>
                    val friendList = friendListData?.map { friendData ->
                        FriendModel(
                            friendId = friendData["friendId"] as String,
                            friendName = friendData["friendName"] as String,
                            friendEmail = friendData["friendEmail"] as String,
                            friendImage = friendData["friendImage"] as String,
                        )
                    } ?: emptyList()

                    if (fullName != null && mail != null && imageUrl != null) {
                        name!!.value = fullName
                        email!!.value = mail
                        imageURL!!.value = imageUrl
                        friendRequest.value = friendReq
                        friends.value = friendList
                    }
                }
            }
        }
    }

    fun addFriend(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val currentUser = getCurrentUser()
            val colRef = firestore.collection("Users")
            val docRefer = colRef.document(currentUser)

            docRefer.get().addOnSuccessListener { doc ->
                val currentUserData = doc.data
                val currentFriends = currentUserData!!["friends"] as? List<HashMap<String, Any>>

                // Verify that the friend is already added
                val isAlreadyFriend = currentFriends?.any { friendData ->
                    friendData["friendId"] == friendId
                } ?: false

                if (!isAlreadyFriend) {
                    colRef.whereEqualTo("userId", friendId).get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                if (friendId != currentUser) {
                                    val friendName = document.getString("userName")!!
                                    val friendImage = document.getString("image")!!
                                    val friendEmail = document.getString("userEmail")!!

                                    val fr =
                                        FriendModel(friendId, friendName, friendEmail, friendImage)

                                    docRefer.update("friends", FieldValue.arrayUnion(fr))
                                        .addOnSuccessListener {
                                            docRefer.get().addOnSuccessListener {
                                                val userName = it.getString("userName")!!

                                                val userEmail = it.getString("userEmail")!!

                                                val userImage = it.getString("image")!!

                                                // FriendModel is used instead of UserModel for data consistency on database
                                                val user =
                                                    FriendModel(
                                                        currentUser,
                                                        userName,
                                                        userEmail,
                                                        userImage,
                                                    )

                                                val friendRef =
                                                    firestore.collection("Users").document(friendId)

                                                friendRef.update(
                                                    "friends",
                                                    FieldValue.arrayUnion(user),
                                                )
                                                    .addOnSuccessListener {
                                                    }
                                            }
                                        }
                                } else {
                                    // cannot add same user
                                }
                            }
                        }
                } else {
                    // already friends
                }
            }
        }
    }
    // verificar si se sigue necesitando el mail del amigo

    fun sendFriendRequest(friendEmail: String, context: Context) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val currentUser = getCurrentUser()
            val colRef = firestore.collection("Users")
            val docRefer = colRef.document(currentUser)

            docRefer.get().addOnSuccessListener { doc ->
                val currentUserData = doc.data
                val currentFriends = currentUserData!!["friends"] as? List<HashMap<String, Any>>

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
                                    docRefer.get().addOnSuccessListener {
                                        val userName = it.getString("userName")
                                        val userEmail = it.getString("userEmail")
                                        val userImage = it.getString("image")

                                        val user =
                                            UserModel(
                                                currentUser,
                                                userName!!,
                                                userEmail!!,
                                                userImage!!,
                                            )

                                        val friendRef =
                                            firestore.collection("Users").document(friendId)

                                        friendRef.update(
                                            "friendRequests",
                                            FieldValue.arrayUnion(user),
                                        )
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Friend request sent",
                                                    Toast.LENGTH_SHORT,
                                                )
                                                    .show()
                                            }
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

    fun discardFriendRequest(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(getCurrentUser())

            docRefer.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val friendRequests =
                        documentSnapshot.get("friendRequests") as? List<Map<String, Any>>

                    if (friendRequests != null) {
                        val updatedFriendRequests = friendRequests.filter { friend ->
                            friend["userId"] != friendId
                        }

                        docRefer.update("friendRequests", updatedFriendRequests)
                            .addOnSuccessListener {
                            }
                    }
                }
            }
        }
    }

    fun deleteFriend(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val user = getCurrentUser()
            val docRefer = firestore.collection("Users").document(getCurrentUser())

            docRefer.get().addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot.exists()) {
                    val friendsList =
                        documentSnapshot.get("friends") as? List<Map<String, Any>>

                    if (friendsList != null) {
                        val updatedFriendList = friendsList.filter { friend ->
                            friend["friendId"] != friendId
                        }

                        docRefer.update("friends", updatedFriendList)
                            .addOnSuccessListener {
                                val friendDocRef = firestore.collection("Users").document(friendId)

                                friendDocRef.get().addOnSuccessListener { friendSnapshot ->
                                    if (friendSnapshot.exists()) {
                                        val friendList =
                                            friendSnapshot.get("friends") as? List<Map<String, Any>>

                                        if (friendList != null) {
                                            val newFriendList = friendList.filter { friends ->
                                                friends["friendId"] != user
                                            }
                                            friendDocRef.update("friends", newFriendList)
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    fun sendMessage(
        conversationId: String,
        messageContent: String,
        senderUid: String,
        user1: String,
        user2: String,
    ) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val message =
                    MessageModel(
                        messageContent,
                        senderUid,
                        "00:00:00",
                        System.currentTimeMillis(),
                        listOf(user1, user2),
                    )
                val messageCollection = firestore.collection("Chats")
                    .document(conversationId)
                    .collection("Messages")

                messageCollection.add(message)
                    .addOnSuccessListener {
                    }
            }
        }

    fun loadMessages(conversationId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val messagesQuery = firestore.collection("Chats")
                .document(conversationId)
                .collection("Messages")
                .orderBy("sorterTime")

            messagesQuery.addSnapshotListener { querySnapshot, _ ->

                val messages = mutableListOf<MessageModel>()

                for (document in querySnapshot!!) {
                    val msg = document.toObject(MessageModel::class.java)
                    messages.add(msg)
                }
                Log.e("", "")

                messageList.value = messages
            }
        }
    }
}
