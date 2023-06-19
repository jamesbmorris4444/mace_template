package com.mace.mace_template.di

import com.mace.mace_template.BloodViewModel
import com.mace.mace_template.repository.RepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val applicationModule = module {
    single { RepositoryImpl() }
    viewModel { BloodViewModel(get()) }
}