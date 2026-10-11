package com.sipinna.atizapan.reportes.di

import com.sipinna.atizapan.reportes.BuildConfig
import com.sipinna.atizapan.reportes.data.remote.ApiService
import com.sipinna.atizapan.reportes.data.remote.interceptors.ErrorInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun proveerMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun proveerOkHttp(moshi: Moshi): OkHttpClient = OkHttpClient.Builder()
        // El interceptor de errores va primero para que traduzca también los
        // fallos de red antes de que los vea el resto de la cadena.
        .addInterceptor(ErrorInterceptor(moshi))
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                // En release no se registra nada: el cuerpo trae datos de la niña,
                // niño o adolescente.
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            },
        )
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        // La subida de fotos en red móvil lenta necesita margen.
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    @Singleton
    fun proveerRetrofit(cliente: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL.trimEnd('/') + "/")
        .client(cliente)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun proveerApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
