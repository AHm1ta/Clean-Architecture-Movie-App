package com.mita.cleanarchitechturemovieapp.di


import com.mita.cleanarchitechturemovieapp.data.repository.MovieRepositoryImpl
import com.mita.cleanarchitechturemovieapp.domain.repository.MovieRepository
import com.mita.cleanarchitechturemovieapp.domain.source.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideDallERepository(
        remoteDataSource: RemoteDataSource,
    ): MovieRepository =
        MovieRepositoryImpl(remoteDataSource)
}