package com.maldEnz.ps.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ItemRecyclerChatListBinding
import com.maldEnz.ps.presentation.activity.ChatActivity
import com.maldEnz.ps.presentation.mvvm.model.ChatModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecentChatsAdapter(private val chatViewModel: ChatViewModel) :
    ListAdapter<ChatModel, RecentChatsAdapter.ChatListViewHolder>(ChatListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val binding = ItemRecyclerChatListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val chatList = getItem(position)
        holder.bind(chatList)
    }

    inner class ChatListViewHolder(val binding: ItemRecyclerChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: ChatModel) {
            binding.apply {
                lastMsgChatList.text = chat.lastMessage

                val messageDateTime = chat.lastMessageDateTime
                val today = Calendar.getInstance()
                val messageDate = Calendar.getInstance()
                messageDate.time = SimpleDateFormat(
                    "HH:mm dd-MM-yyyy",
                    Locale.getDefault(),
                ).parse(messageDateTime)!!

                if (today.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == messageDate.get(Calendar.DAY_OF_YEAR)
                ) {
                    lastMsgDateChatList.text =
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(messageDate.time)
                } else if (today.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) &&
                    today.get(Calendar.DAY_OF_YEAR) == messageDate.get(Calendar.DAY_OF_YEAR) - 1
                ) {
                    lastMsgDateChatList.text = "Yesterday"
                } else {
                    lastMsgDateChatList.text =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(messageDate.time)
                }

                chatViewModel.loadUserData(
                    chat.user1,
                    chat.user2,
                    friendNameChatList,
                    friendImageChatList,
                )

                binding.msgClick.setOnClickListener {
                    if (chat.user1 != FirebaseAuth.getInstance().currentUser!!.uid) {
                        val intent = Intent(it.context, ChatActivity::class.java)
                        intent.putExtra("friendUid", chat.user1)
                        it.context.startActivity(intent)
                    } else {
                        val intent = Intent(it.context, ChatActivity::class.java)
                        intent.putExtra("friendUid", chat.user2)
                        it.context.startActivity(intent)
                    }
                }
            }
        }
    }
}

class ChatListDiffCallback : DiffUtil.ItemCallback<ChatModel>() {
    override fun areItemsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
        return oldItem.chatId == newItem.chatId
    }

    override fun areContentsTheSame(oldItem: ChatModel, newItem: ChatModel): Boolean {
        return oldItem == newItem
    }
}
