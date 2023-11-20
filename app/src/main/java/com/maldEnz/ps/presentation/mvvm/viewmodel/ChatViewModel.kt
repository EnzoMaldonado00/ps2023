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
                        chats.map { map ->
                            val chatId = map["chatId"] as String
                            val lastMessage = map["lastMessage"] as String
                            val lastMessageDateTime = map["lastMessageDateTime"] as String
                            val user1 = map["user1"] as String
                            val user2 = map["user2"] as String
                            val lastMessageTimeStamp = map["lastMessageTimeStamp"] as Long
                            val lastMessageDateTimeZone = map["dateTimeZone"] as String

                            val chat = ChatModel(
                                chatId,
                                lastMessage,
                                lastMessageDateTime,
                                user1,
                                user2,
                                lastMessageTimeStamp,
                                lastMessageDateTimeZone,
                            )

                            val userDoc = if (user1 != currentUser) {
                                firestore.collection("Users").document(user1)
                            } else {
                                firestore.collection("Users").document(user2)
                            }

                            userDoc.get().addOnSuccessListener { userSnapshot ->
                                if (userSnapshot != null && userSnapshot.exists()) {
                                    val userName = userSnapshot["userName"] as String
                                    val userImage = userSnapshot["image"] as String
                                    val userEmail = userSnapshot["userEmail"] as String

                                    val user = UserModel(user1, userName, userEmail, userImage)
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
