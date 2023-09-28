package com.israa.atmodrive.auth.presentation.di

import com.israa.atmodrive.auth.data.datasource.remote.AuthApiService
import com.israa.atmodrive.auth.data.datasource.remote.IRemoteDataSource
import com.israa.atmodrive.auth.data.datasource.remote.RemoteDataSource
import com.israa.atmodrive.auth.data.repo.AuthRepo
import com.israa.atmodrive.auth.domain.repo.IAuthRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object AuthUseCaseModule {


}