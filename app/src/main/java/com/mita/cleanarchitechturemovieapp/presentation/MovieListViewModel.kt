package com.mita.cleanarchitechturemovieapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mita.cleanarchitechturemovieapp.common.utils.Resource
import com.mita.cleanarchitechturemovieapp.data.model.MovieItem
import com.mita.cleanarchitechturemovieapp.data.model.MovieResponse
import com.mita.cleanarchitechturemovieapp.domain.use_case.MovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(private val movieUseCase: MovieUseCase) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<MovieItem>>?>(null)
    val state = _state.asStateFlow()

    fun getMovieList() = viewModelScope.launch {
        movieUseCase.invoke().collect {
            _state.emit(it)
        }

        /*movieUseCase().collect {
            _state.emit(it)
        }*/
    }

}