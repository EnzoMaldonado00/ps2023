package com.maldEnz.ps.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.RecyclerFriendRequestBinding
import com.maldEnz.ps.presentation.mvvm.model.FriendModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel

class FriendRequestAdapter(context: Context) :
    ListAdapter<FriendModel, FriendRequestAdapter.FriendViewHolder>(FriendDiffCallback()) {

    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(context as AppCompatActivity)[UserViewModel::class.java]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = RecyclerFriendRequestBinding.inflate(
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
            userViewModel.discardFriendRequest(friendId)
            userViewModel.addFriend(friendId)
        }
        btnReject.setOnClickListener {
            userViewModel.discardFriendRequest(friendId)
        }
    }

    inner class FriendViewHolder(val binding: RecyclerFriendRequestBinding) :
        ViewHolder(binding.root) {

        fun bind(friend: FriendModel) {
            binding.apply {
                friendProfileName.text = friend.friendName
                friendProfileMail.text = friend.friendEmail

                Glide.with(itemView.context)
                    .load(friend.friendImage)
                    .into(friendProfilePicture)
            }
        }
    }
}

class FriendDiffCallback : DiffUtil.ItemCallback<FriendModel>() {
    override fun areItemsTheSame(oldItem: FriendModel, newItem: FriendModel): Boolean {
        return oldItem.friendId == newItem.friendId
    }

    override fun areContentsTheSame(oldItem: FriendModel, newItem: FriendModel): Boolean {
        return oldItem == newItem
    }
}
