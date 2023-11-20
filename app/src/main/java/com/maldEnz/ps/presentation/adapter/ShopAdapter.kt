package com.maldEnz.ps.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maldEnz.ps.databinding.ItemRecyclerThemesBinding
import com.maldEnz.ps.presentation.mvvm.model.ThemeModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel

class ShopAdapter(private val userViewModel: UserViewModel) :
    ListAdapter<ThemeModel, ShopAdapter.ShopListViewHolder>(ShopDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopListViewHolder {
        val binding = ItemRecyclerThemesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ShopListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopListViewHolder, position: Int) {
        val themeList = getItem(position)
        holder.bind(themeList)
    }

    inner class ShopListViewHolder(val binding: ItemRecyclerThemesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(theme: ThemeModel) {
            binding.apply {
                themeName.text = theme.themeName
                btnBuy.text = theme.price.toString()

                btnBuy.setOnClickListener {
                    userViewModel.buyTheme(theme.themeName, it)
                }
            }
        }
    }
}

class ShopDiffCallback : DiffUtil.ItemCallback<ThemeModel>() {
    override fun areItemsTheSame(oldItem: ThemeModel, newItem: ThemeModel): Boolean {
        return oldItem.themeName == newItem.themeName
    }

    override fun areContentsTheSame(oldItem: ThemeModel, newItem: ThemeModel): Boolean {
        return oldItem == newItem
    }
}
