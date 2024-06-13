package com.mita.cleanarchitechturemovieapp.domain.source

import com.mita.cleanarchitechturemovieapp.data.model.MovieItem


interface RemoteDataSource {
    suspend fun movieList(page: Int, limit: Int): List<MovieItem>
}