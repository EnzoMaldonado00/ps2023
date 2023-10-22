package com.maldEnz.ps.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ActivityChatBinding
import com.maldEnz.ps.presentation.adapter.MessageListAdapter
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.android.ext.android.inject
import java.util.Timer
import java.util.TimerTask

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var currentUserUid: String
    private lateinit var friendUid: String
    private lateinit var friendEmail: String
    private lateinit var chatId: String
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by inject()
    private val friendViewModel: FriendViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        currentUserUid = auth.currentUser!!.uid
        friendUid = intent.getStringExtra("friendUid") ?: ""
        friendEmail = intent.getStringExtra("friendEmail") ?: ""

        userViewModel.getFriendStatus(friendUid)
        friendViewModel.loadFriendData(friendUid, binding.friendName, null, binding.profilePicture)
        userViewModel.getFriendChatState(friendUid)

        val adapter = MessageListAdapter(userViewModel)
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMsg.layoutManager = layoutManager
        binding.recyclerViewMsg.adapter = adapter
        binding.recyclerViewMsg.itemAnimator = null
        layoutManager.stackFromEnd = true

        chatId = generateConversationId(currentUserUid, friendUid)
        userViewModel.loadMessages(chatId)

        userViewModel.messageList.observe(this) {
            adapter.submitList(it)
        }

        binding.btnSend.setOnClickListener {
            val messageContent = binding.msgInput.text.toString()
            val senderId = currentUserUid
            if (messageContent != "" && messageContent.isNotEmpty()) {
                userViewModel.sendMessage(
                    chatId,
                    messageContent,
                    senderId,
                    currentUserUid,
                    friendUid,
                )
                binding.msgInput.text.clear()
            }
        }

        binding.profileClick.setOnClickListener {
            val intent = Intent(it.context, FriendProfileActivity::class.java)
            intent.putExtra("friendUid", friendUid)
            it.context.startActivity(intent)
        }

        friendIsTyping()
        userViewModel.friendStatus.observe(this) { status ->
            binding.status.text = status
        }

        userViewModel.isTyping.observe(this) {
            if (it) {
                binding.status.text = "typing..."
            } else {
                binding.status.text = "online"
            }
        }
    }

    private fun generateConversationId(uid1: String, uid2: String): String {
        val userUids = listOf(uid1, uid2)
        val userIdSorted = userUids.sortedDescending()
        return "${userIdSorted[0]}_${userIdSorted[1]}"
    }

    private fun friendIsTyping() {
        var isTyping = false
        var typingTimer: Timer? = null
        val TYPING_TIMER_DELAY: Long = 4000

        binding.msgInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0 && !isTyping) {
                    isTyping = true
                    userViewModel.userChatState(isTyping, friendUid)

                    typingTimer?.cancel()
                    typingTimer = Timer()
                    typingTimer?.schedule(
                        object : TimerTask() {
                            override fun run() {
                                isTyping = false
                                userViewModel.userChatState(isTyping, friendUid)
                            }
                        },
                        TYPING_TIMER_DELAY,
                    )
                } else if (count == 0 && isTyping) {
                    isTyping = false
                    userViewModel.userChatState(isTyping, friendUid)
                    typingTimer?.cancel()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        userViewModel.updateUserStatusToOnline()
    }

    override fun onPause() {
        super.onPause()
        userViewModel.userChatState(false, friendUid)
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.updateUserStatusToDisconnected()
    }
}
