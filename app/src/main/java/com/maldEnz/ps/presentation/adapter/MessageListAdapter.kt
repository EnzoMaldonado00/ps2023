package com.maldEnz.ps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.databinding.ItemRecyclerReceiverMsgBinding
import com.maldEnz.ps.databinding.ItemRecyclerSenderMsgBinding
import com.maldEnz.ps.presentation.mvvm.model.MessageModel

class MessageListAdapter() :
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
