package com.maldEnz.ps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ItemRecyclerPostCommentsBinding
import com.maldEnz.ps.presentation.mvvm.model.CommentModel

class CommentAdapter :
    ListAdapter<CommentModel, CommentAdapter.PostsViewHolder>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = ItemRecyclerPostCommentsBinding.inflate(
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

    inner class PostsViewHolder(val binding: ItemRecyclerPostCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: CommentModel) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(comment.userImage)
                    .into(profilePicture)

                profileName.text = comment.userName
                commentContent.text = comment.commentContent
                commentDate.text = comment.commentDate
            }
        }
    }
}

class CommentDiffCallback : DiffUtil.ItemCallback<CommentModel>() {
    override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
        return oldItem == newItem
    }
}
