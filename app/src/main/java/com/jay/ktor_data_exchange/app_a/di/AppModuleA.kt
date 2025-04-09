package com.jay.ktor_data_exchange.app_a.di



import com.jay.ktor_data_exchange.app_a.server.KtorServer
import com.jay.ktor_data_exchange.app_a.ui.AppAViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appAModule = module {
    single { KtorServer() }
    viewModel { AppAViewModel(get()) }

}
