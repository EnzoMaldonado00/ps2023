package com.maldEnz.ps.presentation.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.maldEnz.ps.presentation.mvvm.model.FriendModel
import com.maldEnz.ps.presentation.mvvm.model.PostModel
import com.maldEnz.ps.presentation.mvvm.model.UserModel
import com.maldEnz.ps.presentation.util.FunUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!.uid
    private val firestore = FirebaseFirestore.getInstance()
    val friendPostList = MutableLiveData<List<PostModel>>()
    val friend = MutableLiveData<UserModel>()

    fun getFriendPost(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(friendId)

            docRefer.get().addOnSuccessListener {
                val postListData = it.get("posts") as? List<Map<String, Any>>
                val postsList = postListData?.map { postData ->

                    val dateTimeZone = postData["dateTimeZone"] as String
                    val dateTime = postData["dateTime"] as String

                    PostModel(
                        postId = postData["postId"] as String,
                        authorId = postData["authorId"] as String,
                        authorName = postData["authorName"] as String,
                        dateTime = FunUtils.unifyDateTime(dateTime, dateTimeZone),
                        description = postData["description"] as String,
                        imageUrl = postData["imageUrl"] as String,
                        timestamp = postData["timestamp"] as Long,
                        likes = postData["likes"] as List<Map<String, Any>>,
                        comments = postData["comments"] as List<Map<String, Any>>,
                        dateTimeZone = postData["dateTimeZone"] as String,
                    )
                } ?: emptyList()
                val sortedList = postsList.sortedByDescending { postModel -> postModel.timestamp }
                friendPostList.value = sortedList
            }
        }
    }

    fun loadFriendData(friendId: String) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val docRefer = firestore.collection("Users").document(friendId)

                docRefer.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val userData = documentSnapshot.data

                        if (userData != null) {
                            val userName = userData["userName"] as String
                            val userImage = userData["image"] as String
                            val userEmail = userData["userEmail"] as String

                            friend.postValue(
                                UserModel(
                                    friendId,
                                    userName,
                                    userEmail,
                                    userImage,
                                ),
                            )
                        }
                    }
                }
            }
        }

    fun deleteFriend(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

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
                                                friends["friendId"] != currentUser
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

    fun addFriend(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val colRef = firestore.collection("Users")
            val docRefer = colRef.document(currentUser)

            docRefer.get().addOnSuccessListener { doc ->
                val currentUserData = doc.data
                val currentFriends = currentUserData!!["friends"] as? List<HashMap<String, Any>>

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
                                        FriendModel(
                                            friendId,
                                            friendEmail,
                                            false,
                                        )

                                    docRefer.update("friends", FieldValue.arrayUnion(fr))
                                        .addOnSuccessListener {
                                            docRefer.get().addOnSuccessListener {
                                                val userName = it.getString("userName")!!
                                                val userEmail = it.getString("userEmail")!!
                                                val userImage = it.getString("image")!!

                                                val user =
                                                    FriendModel(
                                                        currentUser,
                                                        userEmail,
                                                        false,
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

    fun discardFriendRequest(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

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
}
