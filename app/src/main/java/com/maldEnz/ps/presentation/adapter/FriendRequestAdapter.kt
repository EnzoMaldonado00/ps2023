package com.maldEnz.ps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.maldEnz.ps.databinding.ItemRecyclerFriendRequestBinding
import com.maldEnz.ps.presentation.mvvm.model.FriendModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel

class FriendRequestAdapter(private val friendViewModel: FriendViewModel) :
    ListAdapter<FriendModel, FriendRequestAdapter.FriendViewHolder>(FriendRequestsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemRecyclerFriendRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friendRequests = getItem(position)
        holder.bind(friendRequests)

        val btnAccept = holder.binding.btnAccept
        val btnReject = holder.binding.btnReject
        val friendId = friendRequests.friendId

        btnAccept.setOnClickListener {
            friendViewModel.discardFriendRequest(friendId)
            friendViewModel.addFriend(friendId)
        }
        btnReject.setOnClickListener {
            friendViewModel.discardFriendRequest(friendId)
        }
    }

    inner class FriendViewHolder(val binding: ItemRecyclerFriendRequestBinding) :
        ViewHolder(binding.root) {

        fun bind(friend: FriendModel) {
            binding.apply {
                friendViewModel.loadFriendData(
                    friend.friendId,
                    binding.friendProfileName,
                    friendProfileMail,
                    binding.friendProfilePicture,
                )
            }
        }
    }
}

class FriendRequestsDiffCallback : DiffUtil.ItemCallback<FriendModel>() {
    override fun areItemsTheSame(oldItem: FriendModel, newItem: FriendModel): Boolean {
        return oldItem.friendId == newItem.friendId
    }

    override fun areContentsTheSame(oldItem: FriendModel, newItem: FriendModel): Boolean {
        return oldItem == newItem
    }
}
