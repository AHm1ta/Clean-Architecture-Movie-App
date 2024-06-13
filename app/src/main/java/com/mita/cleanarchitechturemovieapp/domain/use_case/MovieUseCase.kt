package com.mita.cleanarchitechturemovieapp.domain.use_case

import com.mita.cleanarchitechturemovieapp.domain.repository.MovieRepository
import javax.inject.Inject

class MovieUseCase @Inject constructor(private val repository: MovieRepository) {
    operator fun invoke() = repository.movieList(page = 1,limit = 30)
}