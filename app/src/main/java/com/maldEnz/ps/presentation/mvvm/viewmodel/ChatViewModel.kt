package com.maldEnz.ps.presentation.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.maldEnz.ps.presentation.mvvm.model.ChatModel
import com.maldEnz.ps.presentation.mvvm.model.RecentChatModel
import com.maldEnz.ps.presentation.mvvm.model.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser?.uid
    private val firestore = FirebaseFirestore.getInstance()
    val chatList = MutableLiveData<List<RecentChatModel>>()

    fun loadRecentChats() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser!!)

            docRefer.addSnapshotListener { documentSnapshot, _ ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val chats = documentSnapshot.get("chats") as List<Map<String, Any>>?
                    if (chats != null) {
                        val recentChatList = mutableListOf<RecentChatModel>()
                        for (chatData in chats) {
                            val chatId = chatData["chatId"] as String
                            val lastMessage = chatData["lastMessage"] as String
                            val lastMessageDateTime = chatData["lastMessageDateTime"] as String
                            val user1 = chatData["user1"] as String
                            val user2 = chatData["user2"] as String
                            val lastMessageTimeStamp = chatData["lastMessageTimeStamp"] as Long

                            val chat = ChatModel(
                                chatId,
                                lastMessage,
                                lastMessageDateTime,
                                user1,
                                user2,
                                lastMessageTimeStamp,
                            )

                            if (user1 != currentUser) {
                                val user1Refer = firestore.collection("Users").document(user1)

                                user1Refer.addSnapshotListener { user1Snapshot, _ ->
                                    if (user1Snapshot != null && user1Snapshot.exists()) {
                                        val userName = user1Snapshot["userName"] as String
                                        val userImage = user1Snapshot["image"] as String
                                        val userEmail = user1Snapshot["userEmail"] as String

                                        val user = UserModel(user1, userName, userEmail, userImage)
                                        val recentChatModel = RecentChatModel(user, chat)

                                        recentChatList.add(recentChatModel)
                                        chatList.value = recentChatList
                                    }
                                }
                            } else {
                                val user2Refer = firestore.collection("Users").document(user2)

                                user2Refer.addSnapshotListener { user2Snapshot, _ ->
                                    if (user2Snapshot != null && user2Snapshot.exists()) {
                                        val userName = user2Snapshot["userName"] as String
                                        val userImage = user2Snapshot["image"] as String
                                        val userEmail = user2Snapshot["userEmail"] as String

                                        val user = UserModel(user2, userName, userEmail, userImage)
                                        val recentChatModel = RecentChatModel(user, chat)

                                        recentChatList.add(recentChatModel)
                                        val sortedChats =
                                            recentChatList.sortedByDescending { it.chatModel.lastMessageTimeStamp }
                                        chatList.value = sortedChats
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
