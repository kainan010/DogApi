package com.naniak.whatsupdog

import android.app.Application
import com.naniak.whatsupdog.di.databaseModule
import com.naniak.whatsupdog.di.networkModule
import com.naniak.whatsupdog.di.repositoryModule
import com.naniak.whatsupdog.di.useCaseModule
import com.naniak.whatsupdog.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WhatsUpDogApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@WhatsUpDogApp)
            modules(
                networkModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule
            )
        }
    }
}
