package com.sipinna.atizapan.reportes.di

import com.sipinna.atizapan.reportes.model.repository.EvidenciaRepositoryImpl
import com.sipinna.atizapan.reportes.model.repository.ReporteRepositoryImpl
import com.sipinna.atizapan.reportes.model.local.PreferenciasDataStore
import com.sipinna.atizapan.reportes.model.repository.UbicacionRepositoryImpl
import com.sipinna.atizapan.reportes.model.repository.EvidenciaRepository
import com.sipinna.atizapan.reportes.model.repository.PreferenciasRepository
import com.sipinna.atizapan.reportes.model.repository.ReporteRepository
import com.sipinna.atizapan.reportes.model.repository.UbicacionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun enlazarReporteRepository(impl: ReporteRepositoryImpl): ReporteRepository

    @Binds
    @Singleton
    abstract fun enlazarUbicacionRepository(impl: UbicacionRepositoryImpl): UbicacionRepository

    @Binds
    @Singleton
    abstract fun enlazarEvidenciaRepository(impl: EvidenciaRepositoryImpl): EvidenciaRepository

    @Binds
    @Singleton
    abstract fun enlazarPreferenciasRepository(impl: PreferenciasDataStore): PreferenciasRepository

    @Binds
    @Singleton
    abstract fun enlazarAuthRepository(impl: com.sipinna.atizapan.reportes.model.repository.AuthRepositoryImpl): com.sipinna.atizapan.reportes.model.repository.AuthRepository

    companion object {
        /**
         * Dispatcher de E/S inyectado en lugar de usar Dispatchers.IO directo:
         * así las pruebas pueden sustituirlo por uno determinista.
         */
        @Provides
        @Singleton
        fun proveerDispatcherIo(): CoroutineDispatcher = Dispatchers.IO
    }
}
