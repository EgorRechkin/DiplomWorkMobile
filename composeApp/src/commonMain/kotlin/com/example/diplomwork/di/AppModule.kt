// commonMain/kotlin/com/example/diplomwork/di/AppModule.kt
package com.example.diplomwork.di

import com.example.diplomwork.data.ApiClient
import com.example.diplomwork.data.AttendanceRepositoryImpl
import com.example.diplomwork.data.WorkTimeCalculator
import com.example.diplomwork.domain.AttendanceRepository
import com.example.diplomwork.domain.AttendanceUseCase
import com.example.diplomwork.viewmodel.CheckInViewModel
import com.example.diplomwork.viewmodel.NfcViewModel
import com.example.diplomwork.viewmodel.ScheduleViewModel
import com.example.diplomwork.viewmodel.WifiViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { ApiClient() }
    single<AttendanceRepository> { AttendanceRepositoryImpl(get()) }
    single { AttendanceUseCase(get()) }
    single { WorkTimeCalculator() }
    viewModel { CheckInViewModel(get(), get()) }
    viewModel { NfcViewModel() }
    viewModel { WifiViewModel(get()) }
    viewModel { ScheduleViewModel(get()) }


}