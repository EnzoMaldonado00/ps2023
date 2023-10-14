package com.maldEnz.ps.presentation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.RecyclerFriendListBinding
import com.maldEnz.ps.presentation.activity.ChatActivity
import com.maldEnz.ps.presentation.activity.FriendProfileActivity
import com.maldEnz.ps.presentation.mvvm.model.FriendModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel

class FriendListAdapter(context: Context) :
    ListAdapter<FriendModel, FriendListAdapter.FriendListViewHolder>(FriendListDiffCallback()) {

    private val userViewModel: UserViewModel by lazy {
        ViewModelProvider(context as AppCompatActivity)[UserViewModel::class.java]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        val binding = RecyclerFriendListBinding.inflate(
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

        val friendName = friendList.friendName
        val friendId = friendList.friendId
        val friendImageProfile = friendList.friendImage
        val friendEmail = friendList.friendEmail

        btnDelete.setOnClickListener {
            userViewModel.deleteFriend(friendId)
        }
        btnMsg.setOnClickListener {
            val intent = Intent(it.context, ChatActivity::class.java)
            intent.putExtra("friendUid", friendId)
            intent.putExtra("friendName", friendName)
            intent.putExtra("friendImageProfile", friendImageProfile)
            it.context.startActivity(intent)
        }
        friendImage.setOnClickListener {
            val intent = Intent(it.context, FriendProfileActivity::class.java)
            intent.putExtra("friendUid", friendId)
            intent.putExtra("friendName", friendName)
            intent.putExtra("friendImageProfile", friendImageProfile)
            intent.putExtra("friendEmail", friendEmail)
            it.context.startActivity(intent)
        }
    }

    inner class FriendListViewHolder(val binding: RecyclerFriendListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: FriendModel) {
            binding.apply {
                friendProfileName.text = friend.friendName

                Glide.with(itemView.context)
                    .load(friend.friendImage)
                    .into(friendProfilePicture)
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
