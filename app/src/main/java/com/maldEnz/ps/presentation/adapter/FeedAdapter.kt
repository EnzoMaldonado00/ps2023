package com.maldEnz.ps.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ItemRecyclerFeedPostsBinding
import com.maldEnz.ps.presentation.activity.FriendProfileActivity
import com.maldEnz.ps.presentation.mvvm.model.PostModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel

class FeedAdapter(
    private val friendViewModel: FriendViewModel,
) :
    ListAdapter<PostModel, FeedAdapter.FeedViewHolder>(FeedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = ItemRecyclerFeedPostsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val postList = getItem(position)
        holder.bind(postList)

        val profileImage = holder.binding.profilePicture
        val authorId = postList.authorId

        profileImage.setOnClickListener {
            val intent = Intent(it.context, FriendProfileActivity::class.java)
            intent.putExtra("friendUid", authorId)
            it.context.startActivity(intent)
        }
    }

    inner class FeedViewHolder(val binding: ItemRecyclerFeedPostsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostModel) {
            binding.apply {
                friendViewModel.loadFriendData(post.authorId, profileName, null, profilePicture)
                description.text = post.description
                datePosted.text = post.dateTime
                Glide.with(itemView.context)
                    .load(post.imageUrl)
                    .into(imagePost)
            }
        }
    }
}

class FeedDiffCallback : DiffUtil.ItemCallback<PostModel>() {
    override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
        return oldItem == newItem
    }
}
