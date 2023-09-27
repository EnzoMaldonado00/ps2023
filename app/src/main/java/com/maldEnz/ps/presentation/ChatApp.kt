package com.maldEnz.ps.presentation

import android.app.Application
import com.maldEnz.ps.presentation.di.ViewModelModule.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class ChatApp : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ChatApp)

            modules(
                listOf(
                    viewModelModule,
                ),
            )
        }
    }
}
