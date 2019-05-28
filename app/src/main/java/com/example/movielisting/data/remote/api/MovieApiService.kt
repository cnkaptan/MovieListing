package com.example.movielisting.data.remote.api

import com.example.movielisting.data.remote.model.MovieApiResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/popular?language=en-US&region=US")
    fun fetchMoviesPopular(@Query("page") page: Long = 1): Single<MovieApiResponse>

    @GET("movie/top_rated?language=en-US&region=US")
    fun fetchMoviesTopRated(@Query("page") page: Long = 1): Single<MovieApiResponse>

    @GET("movie/upcoming?language=en-US&region=US")
    fun fetchMoviesUpcoming(@Query("page") page: Long = 1): Single<MovieApiResponse>
}