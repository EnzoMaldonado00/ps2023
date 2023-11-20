package com.maldEnz.ps.presentation.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.maldEnz.ps.R
import com.maldEnz.ps.databinding.ItemRecyclerReceiverImageMsgBinding
import com.maldEnz.ps.databinding.ItemRecyclerReceiverImgNoMsgBinding
import com.maldEnz.ps.databinding.ItemRecyclerReceiverMsgBinding
import com.maldEnz.ps.databinding.ItemRecyclerSenderImageMsgBinding
import com.maldEnz.ps.databinding.ItemRecyclerSenderImageNoMsgBinding
import com.maldEnz.ps.databinding.ItemRecyclerSenderMsgBinding
import com.maldEnz.ps.presentation.mvvm.model.MessageModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import com.maldEnz.ps.presentation.util.FunUtils

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

            VIEW_TYPE_SENT_IMAGE -> {
                val binding = ItemRecyclerSenderImageMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
                SentMessageImgViewHolder(binding)
            }

            VIEW_TYPE_SENT_IMAGE_NO_MSG -> {
                val binding = ItemRecyclerSenderImageNoMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
                SentImgNoMsgViewHolder(binding)
            }

            VIEW_TYPE_RECEIVED -> {
                val binding = ItemRecyclerReceiverMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
                ReceivedMessageViewHolder(binding)
            }

            VIEW_TYPE_RECEIVED_IMAGE -> {
                val binding = ItemRecyclerReceiverImageMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
                ReceivedMessageImgViewHolder(binding)
            }

            VIEW_TYPE_RECEIVED_IMAGE_NO_MSG -> {
                val binding = ItemRecyclerReceiverImgNoMsgBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
                ReceivedImgNoMsgViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Error")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is SentMessageImgViewHolder -> holder.bind(message)
            is SentImgNoMsgViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
            is ReceivedMessageImgViewHolder -> holder.bind(message)
            is ReceivedImgNoMsgViewHolder -> holder.bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val msg = getItem(position)
        return if (FirebaseAuth.getInstance().currentUser!!.uid == msg.senderUid) {
            if (msg.imageUrl == null) {
                VIEW_TYPE_SENT
            } else if (msg.content.isNotEmpty() || msg.content != "") {
                VIEW_TYPE_SENT_IMAGE
            } else {
                VIEW_TYPE_SENT_IMAGE_NO_MSG
            }
        } else {
            if (msg.imageUrl == null) {
                VIEW_TYPE_RECEIVED
            } else if (msg.content.isNotEmpty() || msg.content != "") {
                VIEW_TYPE_RECEIVED_IMAGE
            } else {
                VIEW_TYPE_RECEIVED_IMAGE_NO_MSG
            }
        }
    }

    inner class SentMessageViewHolder(private val binding: ItemRecyclerSenderMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.apply {
                msg.text = message.content
                msgDateTime.text = FunUtils.unifyDateTime(message.dateTime, message.dateTimeZone)

                if (message.content != "" && message.content != "Message Deleted") {
                    msg.setOnLongClickListener {
                        showPopupMenu(it, message)
                        true
                    }
                }
            }
        }
    }

    inner class SentMessageImgViewHolder(private val binding: ItemRecyclerSenderImageMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.apply {
                imageMsg.text = message.content
                Glide.with(itemView.context)
                    .load(message.imageUrl)
                    .into(image)
                imgDateTime.text =
                    FunUtils.unifyDateTime(message.dateTime, message.dateTimeZone)

                if (message.content != "Message Deleted") {
                    imageMsg.setOnLongClickListener {
                        showPopupMenu(it, message)
                        true
                    }
                }
            }
        }
    }

    inner class SentImgNoMsgViewHolder(private val binding: ItemRecyclerSenderImageNoMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(message.imageUrl)
                    .into(image)
                imgDateTime.text =
                    FunUtils.unifyDateTime(message.dateTime, message.dateTimeZone)

                if (message.content != "Message Deleted") {
                    image.setOnLongClickListener {
                        showPopupMenu(it, message)
                        true
                    }
                }
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemRecyclerReceiverMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.apply {
                msgReceiver.text = message.content
                msgDateTime.text =
                    FunUtils.unifyDateTime(message.dateTime, message.dateTimeZone)
            }
        }
    }

    inner class ReceivedMessageImgViewHolder(private val binding: ItemRecyclerReceiverImageMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.apply {
                imageMsg.text = message.content
                Glide.with(itemView.context)
                    .load(message.imageUrl)
                    .into(image)
                imgDateTime.text =
                    FunUtils.unifyDateTime(message.dateTime, message.dateTimeZone)
            }
        }
    }

    inner class ReceivedImgNoMsgViewHolder(private val binding: ItemRecyclerReceiverImgNoMsgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(message.imageUrl)
                    .into(image)
                imgDateTime.text =
                    FunUtils.unifyDateTime(message.dateTime, message.dateTimeZone)
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
        private const val VIEW_TYPE_SENT_IMAGE = 3
        private const val VIEW_TYPE_RECEIVED_IMAGE = 4
        private const val VIEW_TYPE_SENT_IMAGE_NO_MSG = 5
        private const val VIEW_TYPE_RECEIVED_IMAGE_NO_MSG = 6
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

class MessageDiffCallback : DiffUtil.ItemCallback<MessageModel>() {
    override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel): Boolean {
        return oldItem == newItem
    }
}
