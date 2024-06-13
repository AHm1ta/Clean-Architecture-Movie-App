package com.mita.cleanarchitechturemovieapp.data.source

import com.mita.cleanarchitechturemovieapp.common.constants.AppConstant.GET_MOVIES_LIST
import com.mita.cleanarchitechturemovieapp.data.model.MovieItem
import com.mita.cleanarchitechturemovieapp.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    @GET(GET_MOVIES_LIST)
    suspend fun getMovies(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int,
    ): List<MovieItem>

    /*@GET("login/cellphone")
    suspend fun login(
        @Query("phone") phone: String?,
        @Query("password") password: String?
    ): Call<>?*/
}