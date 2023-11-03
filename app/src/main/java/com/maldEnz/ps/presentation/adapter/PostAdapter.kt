package com.maldEnz.ps.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ItemRecyclerPostBinding
import com.maldEnz.ps.presentation.activity.PostDetailsActivity
import com.maldEnz.ps.presentation.mvvm.model.PostModel

class PostAdapter :
    ListAdapter<PostModel, PostAdapter.PostsViewHolder>(PostDiffCallback()) {

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

                imagePost.setOnClickListener {
                    val intent = Intent(it.context, PostDetailsActivity::class.java)
                    intent.putExtra("authorId", post.authorId)
                    intent.putExtra("imagePost", post.imageUrl)
                    intent.putExtra("dateTime", post.dateTime)
                    intent.putExtra("description", post.description)
                    intent.putExtra("postId", post.postId)
                    it.context.startActivity(intent)
                }
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
