package com.maldEnz.ps.presentation.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.maldEnz.ps.presentation.mvvm.model.CommentModel
import com.maldEnz.ps.presentation.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class PostViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val currentUser = auth.currentUser!!.uid
    val postComments = MutableLiveData<List<CommentModel>>()

    fun setPostLike(postId: String, authorId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(authorId)
            val userRefer = firestore.collection("Users").document(currentUser)

            docRefer.get().addOnSuccessListener { snapshot ->
                val posts = snapshot["posts"] as? List<HashMap<String, Any>>

                if (posts != null) {
                    val postExists = posts.any { postData ->
                        postData["postId"] == postId
                    }

                    if (postExists) {
                        userRefer.get().addOnSuccessListener { userSnapshot ->
                            val userName = userSnapshot.getString("userName") as String
                            val userEmail = userSnapshot.getString("userEmail") as String
                            val userImage = userSnapshot.getString("image") as String

                            val updatedPostList = posts.map { post ->
                                if (post["postId"] == postId) {
                                    post.toMutableMap().apply {
                                        if (containsKey("likes")) {
                                            val likesList =
                                                get("likes") as ArrayList<HashMap<String, String>>

                                            val userAlreadyLiked = likesList.any { like ->
                                                like["userId"] == currentUser
                                            }

                                            if (!userAlreadyLiked) {
                                                likesList.add(
                                                    hashMapOf(
                                                        "userId" to currentUser,
                                                        "userName" to userName,
                                                        "userEmail" to userEmail,
                                                        "userImage" to userImage,
                                                    ),
                                                )
                                                put("likes", likesList)
                                            } else {
                                                val indexOfUserLike =
                                                    likesList.indexOfFirst { like ->
                                                        like["userId"] == currentUser
                                                    }
                                                if (indexOfUserLike != -1) {
                                                    likesList.removeAt(indexOfUserLike)
                                                    put("likes", likesList)
                                                }
                                            }
                                        } else {
                                            put(
                                                "likes",
                                                arrayListOf(
                                                    hashMapOf(
                                                        "userId" to currentUser,
                                                        "userName" to userName,
                                                        "userEmail" to userEmail,
                                                        "userImage" to userImage,
                                                    ),
                                                ),
                                            )
                                        }
                                    }
                                } else {
                                    post
                                }
                            }
                            docRefer.update("posts", updatedPostList)
                        }
                    }
                }
            }
        }
    }

    fun uploadPostComment(postId: String, authorId: String, commentContent: String) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val docRefer = firestore.collection("Users").document(authorId)
                val userRefer = firestore.collection("Users").document(currentUser)

                docRefer.get().addOnSuccessListener { snapshot ->
                    val posts = snapshot["posts"] as? List<HashMap<String, Any>>

                    if (posts != null) {
                        val postIndex = posts.indexOfFirst { postData ->
                            postData["postId"] == postId
                        }

                        if (postIndex != -1) {
                            userRefer.get().addOnSuccessListener { userSnapshot ->
                                val userName = userSnapshot.getString("userName") as String
                                val userImage = userSnapshot.getString("image") as String

                                val updatedPostList = posts.toMutableList()

                                val postToUpdate = updatedPostList[postIndex].toMutableMap()
                                val commentsList =
                                    postToUpdate.get("comments") as? ArrayList<HashMap<String, Any>>
                                        ?: arrayListOf()

                                val commentId = UUID.randomUUID().toString()

                                commentsList.add(
                                    hashMapOf(
                                        "userName" to userName,
                                        "userImage" to userImage,
                                        "commentDate" to Util.getDateTime(),
                                        "comment" to commentContent,
                                        "commentId" to commentId,
                                        "timeStamp" to System.currentTimeMillis(),
                                    ),
                                )

                                postToUpdate["comments"] = commentsList
                                updatedPostList[postIndex] = postToUpdate as HashMap<String, Any>

                                docRefer.update("posts", updatedPostList)
                            }
                        }
                    }
                }
            }
        }

    fun getPostDetailsComment(postId: String, authorId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(authorId)

            docRefer.addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val posts = snapshot["posts"] as? List<HashMap<String, Any>>

                    if (posts != null) {
                        val postExists = posts.any { postData ->
                            postData["postId"] == postId
                        }

                        if (postExists) {
                            val post = posts.first { it["postId"] == postId }
                            val commentsList = post["comments"] as? List<HashMap<String, Any>>

                            if (commentsList != null) {
                                val commentsModels = commentsList.map { comment ->
                                    CommentModel(
                                        userName = comment["userName"] as String,
                                        userImage = comment["userImage"] as String,
                                        commentDate = comment["commentDate"] as String,
                                        commentContent = comment["comment"] as String,
                                        commentId = comment["commentId"] as String,
                                        timestamp = comment["timeStamp"] as Long,
                                    )
                                }
                                postComments.value = commentsModels
                            }
                        }
                    }
                }
            }
        }
    }
}
