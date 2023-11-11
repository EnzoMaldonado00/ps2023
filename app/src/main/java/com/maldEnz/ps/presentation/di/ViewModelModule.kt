package com.maldEnz.ps.presentation.di

import com.maldEnz.ps.presentation.mvvm.viewmodel.AdminViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.ChatViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.FriendViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.PostViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    val viewModelModule = module {
        viewModel { UserViewModel() }
        viewModel { ChatViewModel() }
        viewModel { FriendViewModel() }
        viewModel { AdminViewModel() }
        viewModel { PostViewModel() }
    }
}
