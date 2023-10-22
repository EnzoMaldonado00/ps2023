package com.maldEnz.ps.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maldEnz.ps.databinding.ItemRecyclerFriendListBinding
import com.maldEnz.ps.presentation.activity.ChatActivity
import com.maldEnz.ps.presentation.activity.FriendProfileActivity
import com.maldEnz.ps.presentation.mvvm.model.FriendModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel

class FriendListAdapter(private val friendViewModel: FriendViewModel) :
    ListAdapter<FriendModel, FriendListAdapter.FriendListViewHolder>(FriendListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        val binding = ItemRecyclerFriendListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return FriendListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        val friendList = getItem(position)
        holder.bind(friendList)

        val btnDelete = holder.binding.btnDelete
        val btnMsg = holder.binding.btnSendMsg
        val friendImage = holder.binding.friendProfilePicture

        val friendId = friendList.friendId

        btnDelete.setOnClickListener {
            friendViewModel.deleteFriend(friendId)
        }
        btnMsg.setOnClickListener {
            val intent = Intent(it.context, ChatActivity::class.java)
            intent.putExtra("friendUid", friendId)
            it.context.startActivity(intent)
        }
        friendImage.setOnClickListener {
            val intent = Intent(it.context, FriendProfileActivity::class.java)
            intent.putExtra("friendUid", friendId)
            it.context.startActivity(intent)
        }
    }

    inner class FriendListViewHolder(val binding: ItemRecyclerFriendListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: FriendModel) {
            binding.apply {
                friendViewModel.loadFriendData(
                    friend.friendId,
                    binding.friendProfileName,
                    null,
                    binding.friendProfilePicture,
                )
            }
        }
    }
}

class FriendListDiffCallback : DiffUtil.ItemCallback<FriendModel>() {
    override fun areItemsTheSame(oldItem: FriendModel, newItem: FriendModel): Boolean {
        return oldItem.friendId == newItem.friendId
    }

    override fun areContentsTheSame(oldItem: FriendModel, newItem: FriendModel): Boolean {
        return oldItem == newItem
    }
}
