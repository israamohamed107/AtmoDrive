package com.israa.atmodrive.auth.presentation.di

import com.israa.atmodrive.auth.data.datasource.remote.AuthApiService
import com.israa.atmodrive.auth.data.datasource.remote.IRemoteDataSource
import com.israa.atmodrive.auth.data.datasource.remote.RemoteDataSource
import com.israa.atmodrive.auth.data.repo.AuthRepo
import com.israa.atmodrive.auth.domain.repo.IAuthRepo
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrive.auth.domain.usecase.IAuthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {

    @Provides
    fun getApiServices(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    fun getIRepo(remoteDataSource: RemoteDataSource):IAuthRepo = AuthRepo(remoteDataSource)
    @Provides
    fun getUseCase(iAuthRepo: IAuthRepo): IAuthUseCase = AuthUseCase(iAuthRepo)

}