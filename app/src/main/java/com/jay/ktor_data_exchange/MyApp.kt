package com.jay.ktor_data_exchange
import android.app.Application
import com.jay.ktor_data_exchange.app_a.di.appAModule
import com.jay.ktor_data_exchange.app_b.di.appBModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(appAModule, appBModule)

        }
    }
}
