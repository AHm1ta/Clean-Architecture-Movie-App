package com.mita.cleanarchitechturemovieapp.data.source


import com.mita.cleanarchitechturemovieapp.data.model.MovieItem
import com.mita.cleanarchitechturemovieapp.domain.source.RemoteDataSource

class RemoteDateSourceImpl (private val remoteService: MovieApiService) : RemoteDataSource {

    override suspend fun movieList(page: Int, limit: Int): List<MovieItem> {
        return remoteService.getMovies(page, limit)
    }

}