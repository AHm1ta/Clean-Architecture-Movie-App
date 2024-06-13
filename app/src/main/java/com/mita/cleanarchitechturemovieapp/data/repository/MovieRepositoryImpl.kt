package com.mita.cleanarchitechturemovieapp.data.repository


import com.mita.cleanarchitechturemovieapp.common.utils.Resource
import com.mita.cleanarchitechturemovieapp.data.model.MovieItem
import com.mita.cleanarchitechturemovieapp.data.model.MovieResponse
import com.mita.cleanarchitechturemovieapp.domain.repository.MovieRepository
import com.mita.cleanarchitechturemovieapp.domain.source.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : MovieRepository {
    override fun movieList(page: Int, limit: Int): Flow<Resource<List<MovieItem>>> = flow{
        emit(Resource.Loading)
        try {
            val response = remoteDataSource.movieList(page, limit)
            emit(Resource.Success(response))
        } catch (t: Throwable) {
            emit(Resource.Error(t))
        }
    }


}
