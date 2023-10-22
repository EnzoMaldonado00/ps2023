package com.maldEnz.ps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ItemRecyclerPostBinding
import com.maldEnz.ps.presentation.mvvm.model.PostModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel

class UserPostAdapter(private val friendViewModel: FriendViewModel) :
    ListAdapter<PostModel, UserPostAdapter.PostsViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = ItemRecyclerPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return PostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val postList = getItem(position)
        holder.bind(postList)
    }

    inner class PostsViewHolder(val binding: ItemRecyclerPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostModel) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(post.imageUrl)
                    .into(imagePost)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<PostModel>() {
    override fun areItemsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: PostModel, newItem: PostModel): Boolean {
        return oldItem == newItem
    }
}
