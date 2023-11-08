package com.israa.atmodrive.home.presentation.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.israa.atmodrive.home.data.datasource.HomeApiServices
import com.israa.atmodrive.home.data.repo.HomeRepository
import com.israa.atmodrive.home.domain.repo.IHomeRepository
import com.israa.atmodrive.home.domain.usecase.HomeUseCase
import com.israa.atmodrive.home.domain.usecase.IHomeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    fun getHomeRepo(homeRepository: HomeRepository):IHomeRepository = homeRepository

    @Provides
    fun getHomeUseCase(homeUseCase: HomeUseCase):IHomeUseCase = homeUseCase

    @Provides
    fun getHomeApiService(retrofit: Retrofit):HomeApiServices = retrofit.create(HomeApiServices::class.java)

    @Provides
    fun getDatabaseRef():DatabaseReference = FirebaseDatabase.getInstance().reference
}