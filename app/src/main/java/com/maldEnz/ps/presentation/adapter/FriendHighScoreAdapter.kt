package com.maldEnz.ps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maldEnz.ps.databinding.ItemRecyclerFriendsScoreBinding
import com.maldEnz.ps.presentation.mvvm.model.ScoreModel

class FriendHighScoreAdapter :
    ListAdapter<ScoreModel, FriendHighScoreAdapter.FriendHighscoreViewHolder>(
        FriendHighScoreListDiffCallback(),
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendHighscoreViewHolder {
        val binding = ItemRecyclerFriendsScoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return FriendHighscoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendHighscoreViewHolder, position: Int) {
        val postList = getItem(position)
        holder.bind(postList)
    }

    inner class FriendHighscoreViewHolder(val binding: ItemRecyclerFriendsScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(score: ScoreModel) {
            binding.apply {
                binding.friendScore.text = score.score
                binding.friendName.text = score.userName
            }
        }
    }
}

class FriendHighScoreListDiffCallback : DiffUtil.ItemCallback<ScoreModel>() {
    override fun areItemsTheSame(oldItem: ScoreModel, newItem: ScoreModel): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: ScoreModel, newItem: ScoreModel): Boolean {
        return oldItem == newItem
    }
}
