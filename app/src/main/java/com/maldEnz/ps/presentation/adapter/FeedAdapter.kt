package com.maldEnz.ps.presentation.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maldEnz.ps.databinding.ItemRecyclerFeedPostsBinding
import com.maldEnz.ps.presentation.activity.FriendProfileActivity
import com.maldEnz.ps.presentation.activity.PostDetailsActivity
import com.maldEnz.ps.presentation.mvvm.model.CommentModel
import com.maldEnz.ps.presentation.mvvm.model.FeedModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.PostViewModel

class FeedAdapter(private val postViewModel: PostViewModel, private val owner: LifecycleOwner) :
    ListAdapter<FeedModel, FeedAdapter.FeedViewHolder>(FeedDiffCallback()) {

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
    }

    inner class FeedViewHolder(val binding: ItemRecyclerFeedPostsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val adapter = CommentAdapter()

        init {
            setupCommentRecyclerView()
        }

        fun bind(feed: FeedModel) {
            binding.apply {
                profileName.text = feed.userModel.userName
                description.text = feed.postModel.description

                Glide.with(itemView.context)
                    .load(feed.userModel.userImage)
                    .into(profilePicture)

                datePosted.text = feed.postModel.dateTime
                Glide.with(itemView.context)
                    .load(feed.postModel.imageUrl)
                    .into(imagePost)

                profilePicture.setOnClickListener {
                    val intent = Intent(it.context, FriendProfileActivity::class.java)
                    intent.putExtra("friendUid", feed.postModel.authorId)
                    it.context.startActivity(intent)
                }

                likesCount.text = feed.postModel.likes.size.toString()

                imagePost.setOnClickListener {
                    val intent = Intent(it.context, PostDetailsActivity::class.java)
                    intent.putExtra("authorId", feed.postModel.authorId)
                    intent.putExtra("imagePost", feed.postModel.imageUrl)
                    intent.putExtra("dateTime", feed.postModel.dateTime)
                    intent.putExtra("description", feed.postModel.description)
                    intent.putExtra("postId", feed.postModel.postId)
                    it.context.startActivity(intent)
                }

                btnLike.setOnClickListener {
                    postViewModel.setPostLike(feed.postModel.postId, feed.postModel.authorId)
                }

                btnComments.setOnClickListener {
                    val intent = Intent(it.context, PostDetailsActivity::class.java)
                    intent.putExtra("authorId", feed.postModel.authorId)
                    intent.putExtra("imagePost", feed.postModel.imageUrl)
                    intent.putExtra("dateTime", feed.postModel.dateTime)
                    intent.putExtra("description", feed.postModel.description)
                    intent.putExtra("postId", feed.postModel.postId)
                    it.context.startActivity(intent)
                }

                val formattedList = feed.postModel.comments.map { comment ->
                    CommentModel(
                        userName = comment["userName"] as String,
                        userImage = comment["userImage"] as String,
                        commentDate = comment["commentDate"] as String,
                        commentContent = comment["comment"] as String,
                        commentId = comment["commentId"] as String,
                        timestamp = comment["timeStamp"] as Long,
                    )
                }

                commentsCount.text = String.format("(%s)", formattedList.size.toString())

                btnSeeComments.setOnClickListener {
                    val intent = Intent(it.context, PostDetailsActivity::class.java)
                    intent.putExtra("authorId", feed.postModel.authorId)
                    intent.putExtra("imagePost", feed.postModel.imageUrl)
                    intent.putExtra("dateTime", feed.postModel.dateTime)
                    intent.putExtra("description", feed.postModel.description)
                    intent.putExtra("postId", feed.postModel.postId)
                    it.context.startActivity(intent)
                }

                if (formattedList.size > 3) {
                    val shortList =
                        formattedList.subList(formattedList.lastIndex - 2, formattedList.lastIndex)
                    adapter.submitList(shortList)
                } else {
                    adapter.submitList(formattedList)
                }

                if (formattedList.isEmpty()) {
                    btnSeeComments.visibility = View.GONE
                }
            }
        }

        private fun setupCommentRecyclerView() {
            binding.recyclerComment.layoutManager = LinearLayoutManager(itemView.context)
            binding.recyclerComment.adapter = adapter
        }
    }
}

class FeedDiffCallback : DiffUtil.ItemCallback<FeedModel>() {
    override fun areItemsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: FeedModel, newItem: FeedModel): Boolean {
        return oldItem == newItem
    }
}
