package com.maldEnz.ps.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ItemRecyclerFriendListBinding
import com.maldEnz.ps.presentation.activity.ChatActivity
import com.maldEnz.ps.presentation.activity.FriendProfileActivity
import com.maldEnz.ps.presentation.mvvm.model.FriendModel
import com.maldEnz.ps.presentation.mvvm.model.UserModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel

class FriendListAdapter(private val friendViewModel: FriendViewModel, private val owner: LifecycleOwner) :
    ListAdapter<UserModel, FriendListAdapter.FriendListViewHolder>(FriendListDiffCallback()) {

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
    }

    inner class FriendListViewHolder(val binding: ItemRecyclerFriendListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: UserModel) {
            binding.apply {
                friendProfileName.text = friend.userName
                Glide.with(itemView.context)
                    .load(friend.userImage)
                    .into(friendProfilePicture)
                btnDelete.setOnClickListener {
                    friendViewModel.deleteFriend(friend.userId)
                }
                btnSendMsg.setOnClickListener {
                    val intent = Intent(it.context, ChatActivity::class.java)
                    intent.putExtra("friendUid", friend.userId)
                    it.context.startActivity(intent)
                }
                friendProfilePicture.setOnClickListener {
                    val intent = Intent(it.context, FriendProfileActivity::class.java)
                    intent.putExtra("friendUid", friend.userId)
                    it.context.startActivity(intent)
                }
            }
        }
    }
}

class FriendListDiffCallback : DiffUtil.ItemCallback<UserModel>() {
    override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
        return oldItem == newItem
    }
}
