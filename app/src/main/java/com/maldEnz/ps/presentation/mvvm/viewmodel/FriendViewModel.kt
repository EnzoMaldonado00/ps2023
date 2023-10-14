package com.maldEnz.ps.presentation.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.maldEnz.ps.presentation.mvvm.model.PostModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    val friendPostList = MutableLiveData<List<PostModel>>()

    fun getFriendPost(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(friendId)

            docRefer.get().addOnSuccessListener {
                val postListData = it.get("posts") as? List<Map<String, Any>>
                val postsList = postListData?.map { postData ->
                    PostModel(
                        authorId = postData["authorId"] as String,
                        authorName = postData["authorName"] as String,
                        dateTime = postData["dateTime"] as String,
                        description = postData["description"] as String,
                        imageUrl = postData["imageUrl"] as String,
                        timestamp = postData["timestamp"] as Long,
                    )
                } ?: emptyList()

                friendPostList.value = postsList
            }
        }
    }
}
