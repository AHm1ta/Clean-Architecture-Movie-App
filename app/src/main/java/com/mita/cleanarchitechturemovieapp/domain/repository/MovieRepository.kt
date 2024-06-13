package com.mita.cleanarchitechturemovieapp.domain.repository

import com.mita.cleanarchitechturemovieapp.common.utils.Resource
import com.mita.cleanarchitechturemovieapp.data.model.MovieItem
import com.mita.cleanarchitechturemovieapp.data.model.MovieResponse
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun movieList(page: Int, limit: Int): Flow<Resource<List<MovieItem>>>
}