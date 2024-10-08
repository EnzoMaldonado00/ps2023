package com.maldEnz.ps.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ItemRecyclerChatListBinding
import com.maldEnz.ps.presentation.activity.ChatActivity
import com.maldEnz.ps.presentation.mvvm.model.RecentChatModel
import com.maldEnz.ps.presentation.util.FunUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RecentChatsAdapter :
    ListAdapter<RecentChatModel, RecentChatsAdapter.ChatListViewHolder>(ChatListDiffCallback()) {

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

        fun bind(recentChat: RecentChatModel) {
            binding.apply {
                if (recentChat.chatModel.lastMessage.isEmpty()) {
                    lastMsgChatList.text = lastMsgChatList.context.getString(R.string.image_text)
                } else {
                    lastMsgChatList.text = recentChat.chatModel.lastMessage
                }

                friendNameChatList.text = recentChat.userModel.userName

                Glide.with(itemView.context)
                    .load(recentChat.userModel.userImage)
                    .into(friendImageChatList)

                val messageDateTime = FunUtils.unifyDateTime(
                    recentChat.chatModel.lastMessageDateTime,
                    recentChat.chatModel.dateTimeZone,
                )

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
                } else {
                    lastMsgDateChatList.text =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(messageDate.time)
                }

                msgClick.setOnClickListener {
                    if (recentChat.chatModel.user1 != FirebaseAuth.getInstance().currentUser!!.uid) {
                        val intent = Intent(it.context, ChatActivity::class.java)
                        intent.putExtra("friendUid", recentChat.chatModel.user1)
                        intent.putExtra("friendName", friendNameChatList.text.toString())
                        intent.putExtra("imageUrl", recentChat.userModel.userImage)
                        it.context.startActivity(intent)
                    } else {
                        val intent = Intent(it.context, ChatActivity::class.java)
                        intent.putExtra("friendUid", recentChat.chatModel.user2)
                        intent.putExtra("friendName", friendNameChatList.text.toString())
                        intent.putExtra("imageUrl", recentChat.userModel.userImage)
                        it.context.startActivity(intent)
                    }
                }
            }
        }
    }
}

class ChatListDiffCallback : DiffUtil.ItemCallback<RecentChatModel>() {
    override fun areItemsTheSame(oldItem: RecentChatModel, newItem: RecentChatModel): Boolean {
        return oldItem.chatModel.chatId == newItem.chatModel.chatId
    }

    override fun areContentsTheSame(oldItem: RecentChatModel, newItem: RecentChatModel): Boolean {
        return oldItem == newItem
    }
}
