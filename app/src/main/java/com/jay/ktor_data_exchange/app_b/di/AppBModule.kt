package com.jay.ktor_data_exchange.app_b.di

import com.jay.ktor_data_exchange.app_b.client.ClientController
import com.jay.ktor_data_exchange.app_b.ui.AppBViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appBModule = module {
    single { ClientController() }
    viewModel { AppBViewModel(get()) }
}