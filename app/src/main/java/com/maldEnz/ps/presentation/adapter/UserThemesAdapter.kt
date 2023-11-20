package com.maldEnz.ps.presentation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.maldEnz.ps.databinding.ItemRecyclerUserThemesBinding
import com.maldEnz.ps.presentation.activity.LogInActivity
import com.maldEnz.ps.presentation.mvvm.model.ThemeModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel

class UserThemesAdapter(private val userViewModel: UserViewModel, private val context: Context) :
    ListAdapter<ThemeModel, UserThemesAdapter.ThemeListViewHolder>(ThemeDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeListViewHolder {
        val binding = ItemRecyclerUserThemesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ThemeListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThemeListViewHolder, position: Int) {
        val themeList = getItem(position)
        holder.bind(themeList)
    }

    inner class ThemeListViewHolder(val binding: ItemRecyclerUserThemesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(theme: ThemeModel) {
            binding.apply {
                themeName.text = theme.themeName
                description.text = theme.description
                btnApply.setOnClickListener {
                    applyTheme(theme.themeName)
                }
            }
        }
    }

    private fun applyTheme(theme: String) {
        val sharedPreferences = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedTheme", theme)
        editor.apply()

        val intent = Intent(context, LogInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}

class ThemeDiffCallback : DiffUtil.ItemCallback<ThemeModel>() {
    override fun areItemsTheSame(oldItem: ThemeModel, newItem: ThemeModel): Boolean {
        return oldItem.themeName == newItem.themeName
    }

    override fun areContentsTheSame(oldItem: ThemeModel, newItem: ThemeModel): Boolean {
        return oldItem == newItem
    }
}
