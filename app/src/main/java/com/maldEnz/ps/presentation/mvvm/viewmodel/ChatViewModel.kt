package com.maldEnz.ps.presentation.mvvm.viewmodel

import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maldEnz.ps.presentation.mvvm.model.ChatModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!.uid
    private val firestore = FirebaseFirestore.getInstance()
    val recentChatList = MutableLiveData<List<ChatModel>>()

    fun loadUserData(user1: String, user2: String, nameTextView: TextView, imageView: ImageView) {
        if (user1 != currentUser) {
            val docRefer = firestore.collection("Users").document(user1)

            docRefer.addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val userData = documentSnapshot.data

                    if (userData != null) {
                        val userName = userData["userName"] as String
                        val userImage = userData["image"] as String
                        nameTextView.text = userName

                        Glide.with(imageView.context)
                            .load(userImage)
                            .into(imageView)
                    }
                }
            }
        } else {
            val docRefer = firestore.collection("Users").document(user2)

            docRefer.addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val userData = documentSnapshot.data

                    if (userData != null) {
                        val userName = userData["userName"] as String
                        val userImage = userData["image"] as String
                        nameTextView.text = userName

                        Glide.with(imageView.context)
                            .load(userImage)
                            .into(imageView)
                    }
                }
            }
        }
    }

    fun loadRecentChats() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

            docRefer.addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val chats = documentSnapshot.get("chats") as List<Map<String, Any>>?
                    if (chats != null) {
                        val chatModels = chats.map { chatMap ->
                            ChatModel(
                                chatId = chatMap["chatId"] as String,
                                lastMessage = chatMap["lastMessage"] as String,
                                lastMessageDateTime = chatMap["lastMessageDateTime"] as String,
                                user1 = chatMap["user1"] as String,
                                user2 = chatMap["user2"] as String,
                                lastMessageTimeStamp = chatMap["lastMessageTimeStamp"] as Long,
                            )
                        }
                        val sortedChats = chatModels.sortedByDescending { it.lastMessageTimeStamp }

                        recentChatList.value = sortedChats
                    }
                }
            }
        }
    }
}
