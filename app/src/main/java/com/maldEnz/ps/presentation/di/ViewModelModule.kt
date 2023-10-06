package com.maldEnz.ps.presentation.di

import com.maldEnz.ps.presentation.mvvm.viewmodel.ChatViewModel
import com.maldEnz.ps.presentation.mvvm.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    val viewModelModule = module {
        viewModel { UserViewModel() }
        viewModel { ChatViewModel() }
    }
}
