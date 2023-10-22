package com.maldEnz.ps.presentation.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ItemRecyclerReceiverMsgBinding
import com.maldEnz.ps.databinding.ItemRecyclerSenderMsgBinding
import com.maldEnz.ps.presentation.mvvm.model.MessageModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel

class MessageListAdapter(private val userViewModel: UserViewModel) :
    ListAdapter<MessageModel, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val binding = ItemRecyclerSenderMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
                SentMessageViewHolder(binding)
            }

            VIEW_TYPE_RECEIVED -> {
                val binding = ItemRecyclerReceiverMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
                ReceivedMessageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Error")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val msg = getItem(position)
        return if (FirebaseAuth.getInstance().currentUser!!.uid == msg.senderUid) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemRecyclerSenderMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.msg.text = message.content
            binding.msgDateTime.text = message.timestamp
            binding.msg.setOnLongClickListener {
                showPopupMenu(it, message)
                true
            }
        }

        private fun showPopupMenu(view: View, message: MessageModel) {
            val popupMenu = PopupMenu(view.context, view)
            val inflater = popupMenu.menuInflater
            inflater.inflate(R.menu.msg_opt_popup_menu, popupMenu.menu)

            popupMenu.gravity = Gravity.CENTER

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_for_everyone -> {
                        userViewModel.deleteMessage(message.conversationId, message.messageId)
                        true
                    }

                    R.id.delete_for_me -> {
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemRecyclerReceiverMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.msgReceiver.text = message.content
            binding.msgDateTime.text = message.timestamp
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<MessageModel>() {
    override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
        return oldItem == newItem
    }
}
