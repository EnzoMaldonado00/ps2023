package com.maldEnz.ps.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ActivityChatBinding
import com.maldEnz.ps.presentation.adapter.MessageListAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var currentUserUid: String
    private lateinit var friendUid: String
    private lateinit var conversationId: String
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUserUid = auth.currentUser!!.uid
        friendUid = intent.getStringExtra("friendUid") ?: ""

        val adapter = MessageListAdapter()
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMsg.layoutManager = layoutManager
        binding.recyclerViewMsg.adapter = adapter
        binding.recyclerViewMsg.itemAnimator = null
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        conversationId = generateConversationId(currentUserUid, friendUid)

        userViewModel.loadMessages(conversationId)

        userViewModel.messageList.observe(this) {
            adapter.submitList(it)
        }

        binding.btnSend.setOnClickListener {
            val messageContent = binding.msgInput.text.toString()
            val senderId = currentUserUid
            if (messageContent != "" && messageContent.isNotEmpty()) {
                userViewModel.sendMessage(conversationId, messageContent, senderId)
            }
        }
    }

    private fun generateConversationId(uid1: String, uid2: String): String {
        val userUids = listOf(uid1, uid2)
        val userIdSorted = userUids.sortedDescending()
        return "${userIdSorted[0]}_${userIdSorted[1]}"
    }
}
