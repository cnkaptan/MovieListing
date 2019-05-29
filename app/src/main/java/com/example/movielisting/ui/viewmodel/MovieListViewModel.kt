package com.example.movielisting.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movielisting.data.local.entity.MovieEntity
import com.example.movielisting.data.remote.api.MovieApiService
import com.example.movielisting.data.repository.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

const val TAG = "MovieListViewModel"

class MovieListViewModel @Inject constructor(movieApiService: MovieApiService) : ViewModel() {

    private val responseDataObservable = PublishSubject.create<Pair<ListType, List<MovieEntity>>>()
    private val movieRepository: MovieRepository = MovieRepository(movieApiService)

    private val subscriptions = CompositeDisposable()

    fun connected(events: Observable<Pair<ListType, Long>>) {
        events.doOnNext { Log.e(TAG, "${it.first} , ${it.second}") }
            .flatMap {
                checkData(it.second, it.first)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                responseDataObservable.onNext(it.first to it.second)
            }

        subscriptions.add(
            checkData(1, POPULAR)
                .doOnNext { Log.e(TAG, "next $POPULAR") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    responseDataObservable.onNext(it)
                }, {
                    Log.e(TAG, it.message, it)
                })
        )
        subscriptions.add(
            checkData(1, TOP_RATED)
                .doOnNext { Log.e(TAG, "next $TOP_RATED") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    responseDataObservable.onNext(it)
                }, {
                    Log.e(TAG, it.message, it)
                })
        )

        subscriptions.add(
            checkData(1, UPCOMING)
                .doOnNext {
                    Log.e(TAG, "next $UPCOMING")
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    responseDataObservable.onNext(it)
                }, {
                    Log.e(TAG, it.message, it)
                })
        )
    }

    fun getScrollResponses(): Observable<Pair<ListType, List<MovieEntity>>> {
        return responseDataObservable.hide()
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }

    fun checkData(needPage: Long, type: ListType): Observable<Pair<ListType, List<MovieEntity>>> {
        return movieRepository.getPopularMovies(needPage, type)
    }

}