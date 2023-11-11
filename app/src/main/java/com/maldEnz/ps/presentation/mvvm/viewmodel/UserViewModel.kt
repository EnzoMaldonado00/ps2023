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
import com.maldEnz.ps.presentation.mvvm.model.ChatModel
import com.maldEnz.ps.presentation.mvvm.model.FeedModel
import com.maldEnz.ps.presentation.mvvm.model.MessageModel
import com.maldEnz.ps.presentation.mvvm.model.PostModel
import com.maldEnz.ps.presentation.mvvm.model.ScoreModel
import com.maldEnz.ps.presentation.mvvm.model.UserModel
import com.maldEnz.ps.presentation.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class UserViewModel : ViewModel() {
    // HANDLE POSSIBLE EXCEPTIONS

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val currentUser = auth.currentUser!!.uid
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val imageUri = MutableLiveData<Uri?>()
    val imageURL = MutableLiveData<String>()
    val friends = MutableLiveData<List<UserModel>>()
    val friendRequest = MutableLiveData<List<UserModel>>()
    val passwordAuth = MutableLiveData<String>()
    val messageList = MutableLiveData<List<MessageModel>>()
    val postList = MutableLiveData<List<PostModel>>()
    val isTyping = MutableLiveData<Boolean>()
    val status = MutableLiveData<String>()
    val friendStatus = MutableLiveData<String>()
    val feedPostList = MutableLiveData<List<FeedModel>>()
    val isAdmin = MutableLiveData<Boolean>()
    val highestScore = MutableLiveData<String>()
    val scoreList = MutableLiveData<List<ScoreModel>>()

    init {
        passwordAuth.value = ""
    }

    fun updateProfileName(context: Context, newName: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            if (newName.isNotEmpty() || newName != "") {
                val documentReference =
                    firestore.collection("Users").document(currentUser)
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
            val imageRef =
                storage.reference.child("profileImages/$currentUser.jpg")

            val uploadTask: UploadTask = imageRef.putFile(imageUri.value!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnCompleteListener { task ->
                    val imageUri = task.result.toString()
                    if (task.isSuccessful) {
                        val documentReference =
                            firestore.collection("Users")
                                .document(currentUser)
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
                    }
                }
            }
        }
    }

    fun getUserData() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users")
                .document(currentUser)

            docRefer.addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val fullName = snapshot.getString("userName")
                    val mail = snapshot.getString("userEmail")
                    val imageUrl = snapshot.getString("image")
                    val admin = snapshot.getBoolean("isAdmin")

                    val postListData = snapshot.get("posts") as? List<Map<String, Any>>
                    val postsList = postListData?.map { postData ->
                        PostModel(
                            authorId = postData["authorId"] as String,
                            authorName = postData["authorName"] as String,
                            dateTime = postData["dateTime"] as String,
                            description = postData["description"] as String,
                            imageUrl = postData["imageUrl"] as String,
                            timestamp = postData["timestamp"] as Long,
                            postId = postData["postId"] as String,
                            likes = postData["likes"] as List<Map<String, Any>>,
                            comments = postData["comments"] as List<Map<String, Any>>,
                        )
                    } ?: emptyList()
                    val sortedList = postsList.sortedByDescending { it.timestamp }

                    if (fullName != null && mail != null && imageUrl != null) {
                        name!!.value = fullName
                        email!!.value = mail
                        imageURL!!.value = imageUrl
                        isAdmin!!.value = admin
                        postList.value = sortedList
                    }
                }
            }
        }
    }

    fun getFriendRequests() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users")
                .document(currentUser)

            docRefer.get().addOnSuccessListener {
                val friendReqList = it.get("friendRequests") as? List<Map<String, Any>>

                if (friendReqList != null) {
                    val friendReqData = mutableListOf<UserModel>()
                    for (friend in friendReqList) {
                        val friendId = friend["userId"] as String

                        val friendDoc = firestore.collection("Users").document(friendId)

                        friendDoc.get().addOnSuccessListener { friendSnapshot ->
                            val friendName = friendSnapshot.get("userName") as String
                            val friendEmail = friendSnapshot.get("userEmail") as String
                            val friendImage = friendSnapshot.get("image") as String

                            val user = UserModel(friendId, friendName, friendEmail, friendImage)
                            friendReqData.add(user)
                            friendRequest.value = friendReqData
                        }
                    }
                }
            }
        }
    }

    fun sendFriendRequest(friendEmail: String, context: Context) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val colRef = firestore.collection("Users")
            val docRefer = colRef.document(currentUser)

            docRefer.get().addOnSuccessListener { doc ->
                val currentUserData = doc.data
                val currentFriends = currentUserData!!["friends"] as? List<HashMap<String, Any>>

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

    fun sendMessage(
        chatId: String,
        messageContent: String,
        senderUid: String,
        user1: String,
        user2: String,
        imageUrl: String?,
    ) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val message =
                    MessageModel(
                        UUID.randomUUID().toString(),
                        chatId,
                        messageContent,
                        senderUid,
                        Util.getDateTime(),
                        System.currentTimeMillis(),
                        listOf(user1, user2),
                        false,
                        imageUrl,
                    )
                val messageCollection = firestore.collection("Chats")
                    .document(chatId)
                    .collection("Messages")

                messageCollection.add(message).addOnSuccessListener {
                    updateChatList(chatId, messageContent, user1, user2)
                }
            }
        }

    private fun updateChatList(
        chatId: String,
        messageContent: String,
        user1: String,
        user2: String,
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val user1Refer = firestore.collection("Users").document(user1)
            val user2Refer = firestore.collection("Users").document(user2)

            user1Refer.get().addOnSuccessListener {
                val user1Data = it.data
                val userChats =
                    user1Data!!["chats"] as? List<HashMap<String, Any>>

                val chatExists = userChats?.any { chatData ->
                    chatData["chatId"] == chatId
                } ?: false

                if (!chatExists) {
                    val chat =
                        ChatModel(
                            chatId,
                            messageContent,
                            Util.getDateTime(),
                            user1,
                            user2,
                            System.currentTimeMillis(),
                        )

                    user1Refer.update("chats", FieldValue.arrayUnion(chat))
                } else {
                    val updatedChatList = userChats!!.map { chat ->
                        if (chat["chatId"] == chatId) {
                            chat.toMutableMap().apply {
                                put("lastMessage", messageContent)
                                put("lastMessageDateTime", Util.getDateTime())
                                put("lastMessageTimeStamp", System.currentTimeMillis())
                            }
                        } else {
                            chat
                        }
                    }
                    user1Refer.update("chats", updatedChatList)
                }
            }
            user2Refer.get().addOnSuccessListener {
                val user2Data = it.data
                val userChats =
                    user2Data!!["chats"] as? List<HashMap<String, Any>>

                val chatExists = userChats?.any { chatData ->
                    chatData["chatId"] == chatId
                } ?: false

                if (!chatExists) {
                    val chat =
                        ChatModel(
                            chatId,
                            messageContent,
                            Util.getDateTime(),
                            user1,
                            user2,
                            System.currentTimeMillis(),
                        )

                    user2Refer.update("chats", FieldValue.arrayUnion(chat))
                } else {
                    val updatedChatList = userChats!!.map { chat ->
                        if (chat["chatId"] == chatId) {
                            chat.toMutableMap().apply {
                                put("lastMessage", messageContent)
                                put("lastMessageDateTime", Util.getDateTime())
                                put("lastMessageTimeStamp", System.currentTimeMillis())
                            }
                        } else {
                            chat
                        }
                    }
                    user2Refer.update("chats", updatedChatList)
                }
            }
        }
    }

    fun loadMessages(chatId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val messageRef = firestore.collection("Chats")
                .document(chatId)
                .collection("Messages")
                .orderBy("sorterTime")

            messageRef.addSnapshotListener { querySnapshot, _ ->

                val messages = mutableListOf<MessageModel>()

                for (document in querySnapshot!!) {
                    if (document.get("deleted") == false) {
                        val msg = document.toObject(MessageModel::class.java)
                        messages.add(msg)
                    }
                }
                messageList.value = messages
            }
        }
    }

    fun deleteMessage(conversationId: String, messageId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val messageRefer = firestore.collection("Chats")
                .document(conversationId)
                .collection("Messages")

            messageRefer.whereEqualTo("messageId", messageId).get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot) {
                        document.reference.update("content", "Message Deleted")
                        document.reference.update("imageUrl", null)
                    }
                }.addOnFailureListener {
                    // error
                }
        }
    }

    fun uploadPost(description: String, imageUri: Uri) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

            val randomUUID = UUID.randomUUID().toString()
            val imageName = "posts/$randomUUID.jpg"
            val imageRef = FirebaseStorage.getInstance().reference.child(imageName)
            val uploadTask: UploadTask = imageRef.putFile(imageUri)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val imageURL = it.result.toString()
                        docRefer.get().addOnSuccessListener {
                            val post = PostModel(
                                randomUUID,
                                currentUser,
                                name.value.toString(),
                                System.currentTimeMillis(),
                                Util.getDateTime(),
                                imageURL,
                                description,
                                emptyList(),
                                emptyList(),
                            )
                            docRefer.update("posts", FieldValue.arrayUnion(post))
                                .addOnSuccessListener {
                                }
                        }
                    }
                }
            }
        }
    }

    // sobrepasa los limites de lectura y escritura de firebase
    // posible solucion: ponerlo en corrutina
    fun userChatState(isTyping: Boolean, friendId: String) {
        val docRefer = firestore.collection("Users")
            .document(friendId)

        docRefer.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot != null) {
                val friendsList = querySnapshot.get("friends") as? List<Map<String, Any>>

                if (friendsList != null) {
                    val updatedFriendsList = friendsList.map { friend ->
                        if (friend["friendId"] == currentUser) {
                            friend.toMutableMap().apply { put("typing", isTyping) }
                        } else {
                            friend
                        }
                    }
                    docRefer.update("friends", updatedFriendsList)
                }
            }
        }
    }

    fun getFriendChatState(friendId: String) {
        val docRefer = firestore.collection("Users")
            .document(currentUser)

        docRefer.addSnapshotListener { querySnapshot, _ ->

            if (querySnapshot != null) {
                val friendsList = querySnapshot.get("friends") as? List<Map<String, Any>>

                if (friendsList != null) {
                    val friendTyping =
                        friendsList.find { it["friendId"] == friendId }?.get("typing") as? Boolean
                    isTyping.value = friendTyping!!
                }
            }
        }
    }

    fun updateUserStatusToOnline() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val statusOnline = "online"
            val docRefer =
                firestore.collection("Users").document(currentUser)
            val updatedStatus = hashMapOf(
                "status" to statusOnline,
            )

            docRefer.update(updatedStatus as Map<String, Any>).addOnSuccessListener {
                status.value = statusOnline
            }
        }
    }

    fun updateUserStatusToDisconnected() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val statusOnline = "last seen ${Util.getDateTime()}"
            val docRefer =
                firestore.collection("Users").document(currentUser)
            val updatedStatus = hashMapOf(
                "status" to statusOnline,
            )

            docRefer.update(updatedStatus as Map<String, Any>).addOnSuccessListener {
                status.value = statusOnline
            }
        }
    }

    fun getFriendStatus(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer =
                firestore.collection("Users").document(friendId)

            docRefer.addSnapshotListener { querySnapshot, _ ->
                if (querySnapshot != null && querySnapshot.exists()) {
                    val frStatus = querySnapshot.get("status") as String
                    friendStatus.value = frStatus
                }
            }
        }
    }

    fun getFeed() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

            docRefer.get().addOnSuccessListener { userSnapshot ->
                if (userSnapshot != null && userSnapshot.exists()) {
                    val friendsList = userSnapshot.get("friends") as? List<Map<String, Any>>

                    if (friendsList != null) {
                        val allFriendPosts = mutableListOf<FeedModel>()

                        for (friendData in friendsList) {
                            val friendId = friendData["friendId"] as String

                            val friendDocRef = firestore.collection("Users").document(friendId)

                            friendDocRef.get().addOnSuccessListener { friendSnapshot ->
                                if (friendSnapshot != null && friendSnapshot.exists()) {
                                    val friendPostsList =
                                        friendSnapshot.get("posts") as? List<Map<String, Any>>

                                    if (friendPostsList != null) {
                                        val friendName = friendSnapshot.get("userName") as String
                                        val friendImage = friendSnapshot.get("image") as String
                                        val friendEmail = friendSnapshot.get("userEmail") as String

                                        val friend = UserModel(
                                            friendId,
                                            friendName,
                                            friendEmail,
                                            friendImage,
                                        )

                                        for (postData in friendPostsList) {
                                            val postId = postData["postId"] as String
                                            val description = postData["description"] as String
                                            val timestamp = postData["timestamp"] as Long
                                            val dateTime = postData["dateTime"] as String
                                            val authorName = postData["authorName"] as String
                                            val imageUrl = postData["imageUrl"] as String
                                            val likes = postData["likes"] as List<Map<String, Any>>
                                            val comments = postData["comments"] as List<Map<String, Any>>

                                            val post = PostModel(
                                                postId,
                                                friendId,
                                                authorName,
                                                timestamp,
                                                dateTime,
                                                imageUrl,
                                                description,
                                                likes,
                                                comments,
                                            )

                                            val feed = FeedModel(friend, post)

                                            allFriendPosts.add(feed)
                                            feedPostList.value = allFriendPosts
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

    fun getHighestScore() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

            docRefer.get().addOnSuccessListener {
                val highScore = it.getString("highestScore") as String
                highestScore.value = highScore
            }
        }
    }

    fun updateHighestScore(score: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

            docRefer.get().addOnSuccessListener {
                val updatedScore = hashMapOf(
                    "highestScore" to score,
                )
                docRefer.update(updatedScore as Map<String, Any>)
            }
        }
    }

    fun getFriendsHighestScore() {
        viewModelScope.launch {
            val docRefer = firestore.collection("Users").document(currentUser)

            docRefer.addSnapshotListener { userSnapshot, _ ->
                if (userSnapshot != null && userSnapshot.exists()) {
                    val friendsList = userSnapshot.get("friends") as? List<Map<String, Any>>
                    val userScore = userSnapshot.get("highestScore") as String
                    val userName = userSnapshot.get("userName") as String
                    val userScoreModel = ScoreModel(
                        userScore,
                        userName,
                    )

                    if (friendsList != null) {
                        val friendsScoreList = mutableListOf<ScoreModel>()
                        friendsScoreList.add(userScoreModel)
                        for (friendData in friendsList) {
                            val friendId = friendData["friendId"] as String

                            val friendDocRef = firestore.collection("Users").document(friendId)

                            friendDocRef.addSnapshotListener { friendSnapshot, _ ->
                                if (friendSnapshot != null && friendSnapshot.exists()) {
                                    val friendName = friendSnapshot.get("userName") as String
                                    val score = friendSnapshot.get("highestScore") as String

                                    val scoreModel = ScoreModel(
                                        score,
                                        friendName,
                                    )

                                    friendsScoreList.add(scoreModel)
                                    friendsScoreList.sortByDescending { it.score }
                                    scoreList.postValue(friendsScoreList)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun loadUserFriends() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

            docRefer.get().addOnSuccessListener {
                val friendList = it.get("friends") as? List<Map<String, Any>>

                if (friendList != null) {
                    val friendData = mutableListOf<UserModel>()
                    for (friend in friendList) {
                        val friendId = friend["friendId"] as String

                        val friendDoc = firestore.collection("Users").document(friendId)

                        friendDoc.get().addOnSuccessListener { friendSnapshot ->
                            val friendName = friendSnapshot.get("userName") as String
                            val friendEmail = friendSnapshot.get("userEmail") as String
                            val friendImage = friendSnapshot.get("image") as String

                            val user = UserModel(friendId, friendName, friendEmail, friendImage)
                            friendData.add(user)
                            friends.value = friendData
                        }
                    }
                }
            }
        }
    }
}
