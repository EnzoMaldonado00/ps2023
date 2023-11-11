package com.maldEnz.ps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ItemRecyclerFriendRequestBinding
import com.maldEnz.ps.presentation.mvvm.model.UserModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel

class FriendRequestAdapter(
    private val friendViewModel: FriendViewModel,
) :
    ListAdapter<UserModel, FriendRequestAdapter.FriendViewHolder>(FriendRequestsDiffCallback()) {

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
    }

    inner class FriendViewHolder(val binding: ItemRecyclerFriendRequestBinding) :
        ViewHolder(binding.root) {

        fun bind(friend: UserModel) {
            binding.apply {
                friendProfileMail.text = friend.userEmail
                Glide.with(itemView.context)
                    .load(friend.userImage)
                    .into(friendProfilePicture)
                friendProfileName.text = friend.userName

                btnAccept.setOnClickListener {
                    friendViewModel.discardFriendRequest(friend.userId)
                    friendViewModel.addFriend(friend.userId)
                }
                btnReject.setOnClickListener {
                    friendViewModel.discardFriendRequest(friend.userId)
                }
            }
        }
    }
}

class FriendRequestsDiffCallback : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem == newItem
    }
}
