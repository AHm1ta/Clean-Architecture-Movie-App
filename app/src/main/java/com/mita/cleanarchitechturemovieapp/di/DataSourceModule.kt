package com.mita.cleanarchitechturemovieapp.di


import com.mita.cleanarchitechturemovieapp.data.source.MovieApiService
import com.mita.cleanarchitechturemovieapp.data.source.RemoteDateSourceImpl
import com.mita.cleanarchitechturemovieapp.domain.source.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideRemoteDateSource(remoteService: MovieApiService): RemoteDataSource =
        RemoteDateSourceImpl(remoteService)
}