package com.racing.raceschedule.data.di

import com.racing.raceschedule.data.repository.RaceRepositoryImpl
import com.racing.raceschedule.presentation.viewmodel.RaceViewModel
import com.racing.raceschedule.data.remote.api.RacingService
import com.racing.raceschedule.domain.repository.RaceRepository
import com.racing.raceschedule.domain.usecase.GetNextRacesUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single<RacingService> {
        Retrofit.Builder()
            .baseUrl("https://api.neds.com.au/rest/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RacingService::class.java)
    }

    single<RaceRepository> { RaceRepositoryImpl(get()) }

    single { GetNextRacesUseCase(get()) }

    viewModel { RaceViewModel(get()) }
}