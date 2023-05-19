package com.mace.mace_template.di

import com.mace.mace_template.repository.RepositoryImpl
import org.koin.dsl.module

val applicationModule = module {
    single { RepositoryImpl() }
//    single { BTRepository }
//    single { DiagnosticsRepository }
//    single { params -> Rom3MediaPlayerManager(callbacks = params.get()) }
//
//    viewModel { MainActivityViewModel(get()) }
//    viewModel { NetworksListViewModel(get()) }
//    viewModel { AccuAngleReminderViewModel(get()) }
//    viewModel { AdjustThePedalsViewModel(get()) }
//    viewModel { BeforeWeBeginViewModel(get()) }
//    viewModel { PainMedicationViewModel(get()) }
//    viewModel { KneeExerciseViewModel(get()) }
//    viewModel { LoginViewModel(get()) }
//    viewModel { MenuLastSessionListViewModel(get()) }
//    viewModel { MenuManualsTopListViewModel(get()) }
//    viewModel { MenuOverallHistoryListViewModel(get()) }
//    viewModel { MenuSettingsListViewModel(get()) }
//    viewModel { CommonTreatmentPlanListViewModel(get(), get()) }
//    viewModel { NowRemoveViewModel(get()) }
//    viewModel { PainLevelViewModel(get()) }
//    viewModel { PedalsReminderViewModel(get()) }
//    viewModel { ReadyToGetStartedViewModel(get()) }
//    viewModel { SessionCompleteViewModel(get()) }
//    viewModel { SpeedometerViewModel(get()) }
//    viewModel { StartSessionViewModel(get()) }
//    viewModel { LoadingPostopViewModel(get()) }
//    viewModel { WelcomeViewModel(get()) }
//    viewModel { SecuritiesListViewModel(get()) }
//    viewModel { MedicationListViewModel(get()) }
//    viewModel { YourMessagesListViewModel(get()) }
//    viewModel { CommonTreatmentPlanFirstItemViewModel(get(), get()) }
//    viewModel { CommonTreatmentPlanSecondItemViewModel(get(), get()) }
//    viewModel { DiagnosticsManufacturingListViewModel(get()) }
//    viewModel { DiagnosticsManufacturingItemViewModel(get()) }
//    viewModel { DiagnosticsManufacturingFirstItemViewModel(get()) }
//    viewModel { MenuRecoveryBadgesListViewModel(get()) }
//    viewModel { MenuRecoveryBadgesFirstItemViewModel(get()) }
//    viewModel { MenuRecoveryBadgesSecondItemViewModel(get()) }
}