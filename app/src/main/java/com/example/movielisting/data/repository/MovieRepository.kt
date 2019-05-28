package com.example.movielisting.data.repository

import com.example.movielisting.data.local.entity.MovieEntity
import com.example.movielisting.data.remote.api.MovieApiService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Singleton
class MovieRepository(private val movieApiService: MovieApiService) {
    fun loadMoviesByType(): Observable<List<MovieEntity>>{
        return movieApiService.fetchMoviesPopular()
            .subscribeOn(Schedulers.io())
            .map { it.results }
            .toObservable()
    }
}
