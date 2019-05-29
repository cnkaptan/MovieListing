package com.example.movielisting.data.repository

import android.util.Log
import com.example.movielisting.data.local.entity.MovieCacheManager
import com.example.movielisting.data.local.entity.MovieEntity
import com.example.movielisting.data.remote.api.MovieApiService
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Singleton

const val TAG = "MovieRepository"

@Singleton
class MovieRepository(private val movieApiService: MovieApiService) {
    private var cachedMovies: MutableMap<ListType, MovieCacheManager> = mutableMapOf()
    private val moviTypeEmitter: Subject<Pair<ListType, List<MovieEntity>>> = PublishSubject.create()

    init {
        cachedMovies[POPULAR] = MovieCacheManager()
        cachedMovies[TOP_RATED] = MovieCacheManager()
        cachedMovies[UPCOMING] = MovieCacheManager()
    }

    fun getPopularMovies(page: Long, type: ListType): Observable<Pair<ListType, List<MovieEntity>>> {
        return Observable.concat(getFromMemory(page, type), getPopularMoviesByApi(page, type))
            .doOnNext {
                Log.e(TAG, "getPopularMovies ${it.first} ${it.second.size}")
//                it.second.forEach {
//                    Log.e(TAG, it.toString())
//                }
            }
            .filter { it.second.isNotEmpty() }
            .first(type to mutableListOf())
            .toObservable()
    }

    fun getFromMemory(page: Long, type: ListType): Observable<Pair<ListType, List<MovieEntity>>> {
        val respCacheManager = cachedMovies[type]!!
        Log.e(TAG, "getPopularMoviesByApi page = page $page , listType = $type")
        return if (page <= 1) {
            getPopularMoviesByApi(type = type)
        } else if (page > respCacheManager.currentPage && page < respCacheManager.totalPage) {
            getPopularMoviesByApi(type = type, page = page)
        } else {
            Observable.just(type to (cachedMovies[type]!!.results) as List<MovieEntity>)
        }
    }

    fun getPopularMoviesByApi(page: Long = 1, type: ListType): Observable<Pair<ListType, List<MovieEntity>>> {
        return when (type) {
            POPULAR -> movieApiService.fetchMoviesPopular(page)
            TOP_RATED -> movieApiService.fetchMoviesTopRated(page)
            UPCOMING -> movieApiService.fetchMoviesUpcoming(page)
        }.subscribeOn(Schedulers.io())
            .doOnSuccess {
                cachedMovies[type]?.addDatas(it)
            }
            .toObservable()
            .flatMap { Observable.just(type to it.results) }
    }
}

sealed class ListType
object POPULAR : ListType()
object TOP_RATED : ListType()
object UPCOMING : ListType()
