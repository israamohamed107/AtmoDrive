package com.israa.atmodrive.di

import MySharedPreferences
import com.israa.atmodrive.auth.data.datasource.remote.AuthApiService
import com.israa.atmodrive.auth.data.datasource.remote.RemoteDataSource
import com.israa.atmodrive.auth.data.repo.AuthRepo
import com.israa.atmodrive.auth.domain.repo.IAuthRepo
import com.israa.atmodrive.auth.domain.usecase.AuthUseCase
import com.israa.atmodrive.auth.domain.usecase.IAuthUseCase
import com.israa.atmodrive.utils.BASE_URL

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(50, TimeUnit.SECONDS)
        .writeTimeout(150, TimeUnit.SECONDS)
        .readTimeout(50, TimeUnit.SECONDS)
        .callTimeout(50, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(object : Interceptor {

            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url
                val url = originalUrl.newBuilder().build()
//                var token:String? = null
//                runBlocking {
//                    token = sharedPreferences.getToken()
//                }
                val requestBuilder = originalRequest.newBuilder().url(url)
                    .addHeader("Accept", "application/json")
                    .addHeader(
                        "Authorization", "Bearer ${MySharedPreferences.getUserToken()}"
                    )
                val request = requestBuilder.build()
                val response = chain.proceed(request) // this fun make the request
                response.code//response status code
                return response
            }
        })
        .build()


    @Provides
    @Singleton
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .build()

    @Provides
    @Singleton
    fun getApiServices(retrofit: Retrofit): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun getIRepo(remoteDataSource: RemoteDataSource):IAuthRepo = AuthRepo(remoteDataSource)
    @Provides
    @Singleton
    fun getUseCase(iAuthRepo: IAuthRepo):IAuthUseCase = AuthUseCase(iAuthRepo)



}