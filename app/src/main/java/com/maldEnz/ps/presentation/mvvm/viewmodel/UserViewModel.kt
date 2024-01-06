package com.maldEnz.ps.presentation.mvvm.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.maldEnz.ps.presentation.mvvm.model.ChatModel
import com.maldEnz.ps.presentation.mvvm.model.FeedModel
import com.maldEnz.ps.presentation.mvvm.model.MessageModel
import com.maldEnz.ps.presentation.mvvm.model.PostModel
import com.maldEnz.ps.presentation.mvvm.model.ScoreModel
import com.maldEnz.ps.presentation.mvvm.model.ThemeModel
import com.maldEnz.ps.presentation.mvvm.model.UserModel
import com.maldEnz.ps.presentation.util.FunUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.TimeZone
import java.util.UUID

class UserViewModel : ViewModel() {
    // HANDLE POSSIBLE EXCEPTIONS

    var auth = FirebaseAuth.getInstance()
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
    val friendStatusTimeZone = MutableLiveData<String>()
    val feedPostList = MutableLiveData<List<FeedModel>>()
    val isAdmin = MutableLiveData<Boolean>()
    val highestScore = MutableLiveData<String>()
    val scoreList = MutableLiveData<List<ScoreModel>>()
    val coins = MutableLiveData<Long>()
    val themesList = MutableLiveData<List<ThemeModel>>()
    val unlockedThemes = MutableLiveData<List<ThemeModel>>()
    val theme = MutableLiveData<Int>()
    val friendToken = MutableLiveData<String>()

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
                    val userCoins = snapshot.getLong("coins")

                    val postListData = snapshot.get("posts") as? List<Map<String, Any>>
                    val postsList = postListData?.map { postData ->

                        val dateTime = postData["dateTime"] as String
                        val dateTimeZone = postData["dateTimeZone"] as String

                        PostModel(
                            authorId = postData["authorId"] as String,
                            authorName = postData["authorName"] as String,
                            dateTime = FunUtils.unifyDateTime(dateTime, dateTimeZone),
                            description = postData["description"] as String,
                            imageUrl = postData["imageUrl"] as String,
                            timestamp = postData["timestamp"] as Long,
                            postId = postData["postId"] as String,
                            likes = postData["likes"] as List<Map<String, Any>>,
                            comments = postData["comments"] as List<Map<String, Any>>,
                            dateTimeZone = postData["dateTimeZone"] as String,
                        )
                    } ?: emptyList()
                    val sortedList = postsList.sortedByDescending { it.timestamp }

                    if (fullName != null && mail != null && imageUrl != null) {
                        name!!.value = fullName
                        email!!.value = mail
                        imageURL!!.value = imageUrl
                        isAdmin!!.value = admin
                        postList.value = sortedList
                        coins!!.value = userCoins
                    }
                }
            }
        }
    }

    fun getFriendRequests() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

            docRefer.addSnapshotListener { it, _ ->
                if (it != null && it.exists()) {
                    val friendReqList = it.get("friendRequests") as? List<Map<String, Any>>

                    if (friendReqList != null) {
                        val friendReqCounts = friendReqList.size
                        var usersLoaded = 0
                        val friendReqData = mutableListOf<UserModel>()
                        friendReqList.forEach { friend ->
                            val friendId = friend["userId"] as String

                            val friendDoc = firestore.collection("Users").document(friendId)

                            friendDoc.get().addOnSuccessListener { friendSnapshot ->
                                val friendName = friendSnapshot.get("userName") as String
                                val friendEmail = friendSnapshot.get("userEmail") as String
                                val friendImage = friendSnapshot.get("image") as String

                                val user = UserModel(friendId, friendName, friendEmail, friendImage)
                                friendReqData.add(user)
                                usersLoaded++
                                if (friendReqCounts == usersLoaded) {
                                    friendRequest.value = friendReqData
                                }
                            }
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
        friendToken: String,
    ) =
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val message =
                    MessageModel(
                        UUID.randomUUID().toString(),
                        chatId,
                        messageContent,
                        senderUid,
                        FunUtils.getDateTime(),
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
                    sendNotification(
                        messageContent,
                        friendToken,
                        name.value!!,
                        currentUser,
                        imageURL.value!!,
                    )
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

            user1Refer.get().addOnSuccessListener { user1Snapshot ->
                val user1Data = user1Snapshot.data
                val userChats =
                    user1Data?.get("chats") as? List<HashMap<String, Any>>

                val chatIndex = userChats?.indexOfFirst { chatData ->
                    chatData["chatId"] == chatId
                } ?: -1

                if (chatIndex == -1) {
                    val chat =
                        ChatModel(
                            chatId,
                            messageContent,
                            FunUtils.getDateTime(),
                            user1,
                            user2,
                            System.currentTimeMillis(),
                            TimeZone.getDefault().id.toString(),
                        )

                    user1Refer.update("chats", FieldValue.arrayUnion(chat))
                } else {
                    val updatedChatList = userChats?.mapIndexed { index, chat ->
                        if (index == chatIndex) {
                            chat.toMutableMap().apply {
                                put("lastMessage", messageContent)
                                put("lastMessageDateTime", FunUtils.getDateTime())
                                put("lastMessageTimeStamp", System.currentTimeMillis())
                                put("dateTimeZone", TimeZone.getDefault().id.toString())
                            }
                        } else {
                            chat
                        }
                    }
                    updatedChatList?.let {
                        user1Refer.update("chats", it)
                    }
                }
            }

            user2Refer.get().addOnSuccessListener { user2Snapshot ->
                val user2Data = user2Snapshot.data
                val userChats =
                    user2Data?.get("chats") as? List<HashMap<String, Any>>

                val chatIndex = userChats?.indexOfFirst { chatData ->
                    chatData["chatId"] == chatId
                } ?: -1

                if (chatIndex == -1) {
                    val chat =
                        ChatModel(
                            chatId,
                            messageContent,
                            FunUtils.getDateTime(),
                            user1,
                            user2,
                            System.currentTimeMillis(),
                        )

                    user2Refer.update("chats", FieldValue.arrayUnion(chat))
                } else {
                    val updatedChatList = userChats?.mapIndexed { index, chat ->
                        if (index == chatIndex) {
                            chat.toMutableMap().apply {
                                put("lastMessage", messageContent)
                                put("lastMessageDateTime", FunUtils.getDateTime())
                                put("lastMessageTimeStamp", System.currentTimeMillis())
                                put("dateTimeZone", TimeZone.getDefault().id.toString())
                            }
                        } else {
                            chat
                        }
                    }
                    updatedChatList?.let {
                        user2Refer.update("chats", it)
                    }
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
                                FunUtils.getDateTime(),
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
            val statusOnline = FunUtils.getDateTime()
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
                    val frStatusTimeZone = querySnapshot.get("statusTimeZone") as String
                    friendStatus.value = frStatus
                    friendStatusTimeZone.value = frStatusTimeZone
                }
            }
        }
    }

    fun getFeed() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)
            var totalFriendsPosts = 0
            var totalPosts = 0

            docRefer.get().addOnSuccessListener { userSnapshot ->
                if (userSnapshot != null && userSnapshot.exists()) {
                    val friendsList = userSnapshot.get("friends") as? List<Map<String, Any>>

                    val feedPosts = mutableListOf<FeedModel>()

                    val userPostsList =
                        userSnapshot.get("posts") as? List<Map<String, Any>>
                    if (userPostsList != null) {
                        val userName = userSnapshot.get("userName") as String
                        val userImage = userSnapshot.get("image") as String
                        val userEmail = userSnapshot.get("userEmail") as String

                        val user = UserModel(
                            currentUser,
                            userName,
                            userEmail,
                            userImage,
                        )

                        for (postData in userPostsList) {
                            val postId = postData["postId"] as String
                            val description = postData["description"] as String
                            val timestamp = postData["timestamp"] as Long
                            val dateTime = postData["dateTime"] as String
                            val authorName = postData["authorName"] as String
                            val imageUrl = postData["imageUrl"] as String
                            val likes = postData["likes"] as List<Map<String, Any>>
                            val comments =
                                postData["comments"] as List<Map<String, Any>>

                            val dateTimeZone = postData["dateTimeZone"] as String

                            val post = PostModel(
                                postId,
                                currentUser,
                                authorName,
                                timestamp,
                                FunUtils.unifyDateTime(dateTime, dateTimeZone),
                                imageUrl,
                                description,
                                likes,
                                comments,
                            )

                            val feed = FeedModel(user, post)
                            feedPosts.add(feed)
                            totalPosts++
                        }

                        if (!friendsList.isNullOrEmpty()) {
                            for (friendData in friendsList) {
                                val friendId = friendData["friendId"] as String

                                val friendDocRef = firestore.collection("Users").document(friendId)

                                friendDocRef.get().addOnSuccessListener { friendSnapshot ->
                                    if (friendSnapshot != null && friendSnapshot.exists()) {
                                        val friendPostsList =
                                            friendSnapshot.get("posts") as? List<Map<String, Any>>

                                        if (friendPostsList != null) {
                                            totalFriendsPosts += friendPostsList.size

                                            val friendName =
                                                friendSnapshot.get("userName") as String
                                            val friendImage = friendSnapshot.get("image") as String
                                            val friendEmail =
                                                friendSnapshot.get("userEmail") as String

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
                                                val likes =
                                                    postData["likes"] as List<Map<String, Any>>
                                                val comments =
                                                    postData["comments"] as List<Map<String, Any>>

                                                val dateTimeZone =
                                                    postData["dateTimeZone"] as String

                                                val post = PostModel(
                                                    postId,
                                                    friendId,
                                                    authorName,
                                                    timestamp,
                                                    FunUtils.unifyDateTime(dateTime, dateTimeZone),
                                                    imageUrl,
                                                    description,
                                                    likes,
                                                    comments,
                                                )

                                                val feed = FeedModel(friend, post)

                                                feedPosts.add(feed)
                                                totalPosts++
                                            }
                                        }
                                    }
                                    feedPostList.value = feedPosts
                                }
                            }
                        } else {
                            feedPostList.value = emptyList()
                        }
                    }
                }
            }
        }
    }

    private suspend fun getUserPosts(currentUser: String): List<FeedModel> {
        val userPosts = mutableListOf<FeedModel>()
        val docRefer = firestore.collection("Users").document(currentUser).get().await()

        if (docRefer != null && docRefer.exists()) {
            val userPostsList =
                docRefer.get("posts") as? List<Map<String, Any>>
            if (userPostsList != null) {
                val userName = docRefer.get("userName") as String
                val userImage = docRefer.get("image") as String
                val userEmail = docRefer.get("userEmail") as String

                val user = UserModel(
                    currentUser,
                    userName,
                    userEmail,
                    userImage,
                )

                for (postData in userPostsList) {
                    val postId = postData["postId"] as String
                    val description = postData["description"] as String
                    val timestamp = postData["timestamp"] as Long
                    val dateTime = postData["dateTime"] as String
                    val authorName = postData["authorName"] as String
                    val imageUrl = postData["imageUrl"] as String
                    val likes = postData["likes"] as List<Map<String, Any>>
                    val comments =
                        postData["comments"] as List<Map<String, Any>>

                    val dateTimeZone = postData["dateTimeZone"] as String

                    val post = PostModel(
                        postId,
                        currentUser,
                        authorName,
                        timestamp,
                        FunUtils.unifyDateTime(dateTime, dateTimeZone),
                        imageUrl,
                        description,
                        likes,
                        comments,
                    )

                    val feed = FeedModel(user, post)
                    userPosts.add(feed)
                }
            }
        }
        return userPosts
    }

    private suspend fun getFriendPosts(friendsList: List<Map<String, Any>>): List<FeedModel> {
        val friendPosts = mutableListOf<FeedModel>()

        if (friendsList.isNotEmpty()) {
            for (friendData in friendsList) {
                val friendId = friendData["friendId"] as String

                val friendDocRef = firestore.collection("Users").document(friendId).get().await()

                if (friendDocRef != null && friendDocRef.exists()) {
                    val friendPostsList =
                        friendDocRef.get("posts") as? List<Map<String, Any>>

                    if (friendPostsList != null) {
                        val friendName =
                            friendDocRef.get("userName") as String
                        val friendImage = friendDocRef.get("image") as String
                        val friendEmail =
                            friendDocRef.get("userEmail") as String

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
                            val likes =
                                postData["likes"] as List<Map<String, Any>>
                            val comments =
                                postData["comments"] as List<Map<String, Any>>

                            val dateTimeZone =
                                postData["dateTimeZone"] as String

                            val post = PostModel(
                                postId,
                                friendId,
                                authorName,
                                timestamp,
                                FunUtils.unifyDateTime(dateTime, dateTimeZone),
                                imageUrl,
                                description,
                                likes,
                                comments,
                            )

                            val feed = FeedModel(friend, post)
                            friendPosts.add(feed)
                        }
                    }
                }
            }
        }
        return friendPosts
    }

    suspend fun getFeedAlt(currentUser: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val userPosts = getUserPosts(currentUser)
                val docRefer = firestore.collection("Users").document(currentUser).get().await()
                val friendsList = docRefer.get("friends") as? List<Map<String, Any>> ?: emptyList()
                val friendPosts = getFriendPosts(friendsList)

                val allPosts = userPosts + friendPosts

                feedPostList.postValue(allPosts)
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

            docRefer.get().addOnSuccessListener { userSnapshot ->
                if (userSnapshot != null && userSnapshot.exists()) {
                    val friendsList =
                        userSnapshot.get("friends") as? List<Map<String, Any>>
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

                            val friendDocRef =
                                firestore.collection("Users").document(friendId)

                            friendDocRef.get().addOnSuccessListener { friendSnapshot ->
                                if (friendSnapshot != null && friendSnapshot.exists()) {
                                    val friendName =
                                        friendSnapshot.get("userName") as String
                                    val score =
                                        friendSnapshot.get("highestScore") as String

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

            docRefer.addSnapshotListener { it, _ ->
                if (it != null && it.exists()) {
                    val friendList = it.get("friends") as? List<Map<String, Any>>

                    if (!friendList.isNullOrEmpty()) {
                        val friendData = mutableListOf<UserModel>()

                        val friendCount = friendList.size
                        var friendsLoaded = 0

                        friendList.forEach { friend ->
                            val friendId = friend["friendId"] as String
                            val friendDoc =
                                firestore.collection("Users").document(friendId)

                            friendDoc.get().addOnSuccessListener { friendSnapshot ->
                                val friendName =
                                    friendSnapshot.get("userName") as String
                                val friendEmail =
                                    friendSnapshot.get("userEmail") as String
                                val friendImage = friendSnapshot.get("image") as String

                                val user = UserModel(
                                    friendId,
                                    friendName,
                                    friendEmail,
                                    friendImage,
                                )
                                friendData.add(user)

                                friendsLoaded++
                                if (friendsLoaded == friendCount) {
                                    friends.value = friendData
                                }
                            }
                        }
                    } else {
                        friends.value = emptyList()
                    }
                }
            }
        }
    }

    fun convertScoreToCoins(score: Int) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users").document(currentUser)

            docRefer.get().addOnSuccessListener {
                val userCoins = it.get("coins") as Int

                val coinsEarned = score / 500 * 10

                val updatedCoins = userCoins + coinsEarned

                docRefer.update("coins", updatedCoins)
            }
        }
    }

    fun getThemes() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Themes")

            docRefer.get().addOnSuccessListener { snapshot ->
                val themes = snapshot.map { map ->
                    ThemeModel(
                        themeName = map.get("themeName") as String,
                        description = map.get("description") as String,
                        price = map.get("price") as Long,
                        timesUnlocked = map.get("timesUnlocked") as Long,
                    )
                }
                themesList.value = themes
            }
        }
    }

    fun buyTheme(themeName: String, view: View) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val userRefer = firestore.collection("Users").document(currentUser)
            val themeRefer = firestore.collection("Themes")

            userRefer.get().addOnSuccessListener { userSnapshot ->
                val coins = userSnapshot.get("coins") as Long
                val userThemes =
                    userSnapshot.get("themesUnlocked") as? List<Map<String, Any>>
                        ?: emptyList()

                unlockedThemes.value

                val isThemeUnlocked = userThemes.any { it["themeName"] == themeName }

                if (isThemeUnlocked) {
                    Toast.makeText(
                        view.context,
                        "Theme already unlocked",
                        Toast.LENGTH_SHORT,
                    )
                        .show()
                } else {
                    themeRefer.whereEqualTo("themeName", themeName).get()
                        .addOnSuccessListener { themeSnapshot ->

                            val price = themeSnapshot.documents[0].get("price") as Long

                            if (coins >= price) {
                                val themeId = themeSnapshot.documents[0].id
                                val timesUnlocked =
                                    themeSnapshot.documents[0].get("timesUnlocked") as Long
                                themeSnapshot.documents.map { map ->
                                    val theme = hashMapOf(
                                        "themeName" to map.get("themeName") as String,
                                        "description" to map.get("description") as String,
                                        "price" to map.get("price") as Long,
                                    )

                                    themeRefer.document(themeId)
                                        .update("timesUnlocked", timesUnlocked + 1)
                                    userRefer.update(
                                        "themesUnlocked",
                                        FieldValue.arrayUnion(theme),
                                    )
                                    userRefer.update("coins", coins - price)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                view.context,
                                                "Unlocked!",
                                                Toast.LENGTH_SHORT,
                                            )
                                                .show()
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    view.context,
                                    "No enough coins",
                                    Toast.LENGTH_SHORT,
                                )
                                    .show()
                            }
                        }
                }
            }
        }
    }

    fun getUserThemes() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRefer = firestore.collection("Users")
                .document(currentUser)

            docRefer.addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val userThemes =
                        snapshot.get("themesUnlocked") as? List<Map<String, Any>>
                    val themesList = userThemes?.map { themeData ->
                        ThemeModel(
                            themeName = themeData["themeName"] as String,
                            description = themeData["description"] as String,
                            price = 0,
                            timesUnlocked = 0,
                        )
                    } ?: emptyList()

                    unlockedThemes.value = themesList
                }
            }
        }
    }

    fun setUserToken() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    val docRef = firestore.collection("Users")
                        .document(currentUser)
                    Log.d("token", token)
                    docRef.update("fcmToken", token)
                }
            }
        }
    }

    fun getUserToken(friendId: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val docRef = firestore.collection("Users").document(friendId)

            docRef.get().addOnSuccessListener {
                val frToken = it.getString("fcmToken") as String
                friendToken.value = frToken
            }
        }
    }

    private fun sendNotification(
        message: String,
        friendToken: String,
        userName: String,
        currentUser: String,
        profileImage: String,
    ) {
        val jsonObject = JSONObject()

        val notificationObj = JSONObject()
        notificationObj.put("title", userName)
        notificationObj.put("body", message)
        notificationObj.put("profilePicture", profileImage)

        val dataObj = JSONObject()
        dataObj.put("userId", currentUser)

        jsonObject.put("notification", notificationObj)
        jsonObject.put("data", dataObj)
        jsonObject.put("to", friendToken)

        callApi(jsonObject)
    }

    private fun callApi(jsonObject: JSONObject) {
        val json = "application/json; charset=utf-8".toMediaTypeOrNull()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body = jsonObject.toString().toRequestBody(json)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header(
                "Authorization",
                "Bearer KEY", // API_KEY FCM
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
            }
        })
    }
}
