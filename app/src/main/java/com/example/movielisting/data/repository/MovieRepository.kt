package com.example.movielisting.data.repository

import android.util.Log
import com.example.movielisting.data.local.entity.*
import com.example.movielisting.data.remote.api.MovieApiService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

const val TAG = "MovieRepository"

@Singleton
class MovieRepository(private val movieApiService: MovieApiService) {
    private var cachedMovies: MutableMap<ListType, MovieCacheManager> = mutableMapOf()

    init {
        cachedMovies[POPULAR] = MovieCacheManager()
        cachedMovies[TOP_RATED] = MovieCacheManager()
        cachedMovies[UPCOMING] = MovieCacheManager()
    }

    fun getPopularMovies(movieTypeRequestEvent: MovieTypeRequestEvent): Observable<MovieTypeResponseEvent> {
        return Observable.concat(
            getFromMemoryCache(movieTypeRequestEvent),
            getPopularMoviesByApi(movieTypeRequestEvent)
        )
            .filter { it.result.isNotEmpty() }
            .first(MovieTypeResponseEvent(movieTypeRequestEvent.type, mutableListOf()))
            .toObservable()

    }

    fun getFromMemoryCache(movieTypeRequestEvent: MovieTypeRequestEvent): Observable<MovieTypeResponseEvent> {
        val respCacheManager = cachedMovies[movieTypeRequestEvent.type]!!
        return if (movieTypeRequestEvent.page > respCacheManager.currentPage) {
            Observable.empty()
        } else {
            val results = cachedMovies[movieTypeRequestEvent.type]!!.results as List<MovieEntity>
            Observable.just(MovieTypeResponseEvent(movieTypeRequestEvent.type, results))
        }
    }

    fun getPopularMoviesByApi(movieTypeRequestEvent: MovieTypeRequestEvent): Observable<MovieTypeResponseEvent> {
        return when (movieTypeRequestEvent.type) {
            POPULAR -> movieApiService.fetchMoviesPopular(movieTypeRequestEvent.page)
            TOP_RATED -> movieApiService.fetchMoviesTopRated(movieTypeRequestEvent.page)
            UPCOMING -> movieApiService.fetchMoviesUpcoming(movieTypeRequestEvent.page)
        }.subscribeOn(Schedulers.io())
            .doOnSuccess {
                cachedMovies[movieTypeRequestEvent.type]?.addDatas(it)
            }
            .toObservable()
            .flatMap { Observable.just(MovieTypeResponseEvent(movieTypeRequestEvent.type, it.results)) }
    }
}
