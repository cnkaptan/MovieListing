package com.example.movielisting.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.movielisting.data.Resource
import com.example.movielisting.data.local.entity.*
import com.example.movielisting.data.remote.api.MovieApiService
import com.example.movielisting.data.repository.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

const val TAG = "MovieListViewModel"

class MovieListViewModel @Inject constructor(movieApiService: MovieApiService) : ViewModel() {

    private val responseDataObservable = PublishSubject.create<Resource<MovieTypeResponseEvent>>()
    private val movieRepository: MovieRepository = MovieRepository(movieApiService)

    private val subscriptions = CompositeDisposable()

    fun connected(events: Observable<MovieTypeRequestEvent>) {
        events
            .flatMap {
                responseDataObservable.onNext(Resource.loading(MovieTypeResponseEvent(it.type, emptyList())))
                checkData(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                responseDataObservable.onNext(Resource.success(it))
            }

        subscriptions.add(
            Observable.just(MovieTypeRequestEvent(POPULAR,1))
                .doOnNext {
                    responseDataObservable.onNext(Resource.loading(MovieTypeResponseEvent(it.type, emptyList())))
                }
                .flatMap { checkData(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    responseDataObservable.onNext(Resource.success(it))
                }, {
                    Log.e(TAG, it.message, it)
                })
        )
        subscriptions.add(
            Observable.just(MovieTypeRequestEvent(TOP_RATED,1))
                .doOnNext {
                    responseDataObservable.onNext(Resource.loading(MovieTypeResponseEvent(it.type, emptyList())))
                }
                .flatMap { checkData(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    responseDataObservable.onNext(Resource.success(it))
                }, {
                    Log.e(TAG, it.message, it)
                })
        )

        subscriptions.add(
            Observable.just(MovieTypeRequestEvent(UPCOMING,1))
                .doOnNext {
                    responseDataObservable.onNext(Resource.loading(MovieTypeResponseEvent(it.type, emptyList())))
                }
                .flatMap { checkData(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    responseDataObservable.onNext(Resource.success(it))
                }, {
                    Log.e(TAG, it.message, it)
                })
        )
    }

    fun getScrollResponses(): Observable<Resource<MovieTypeResponseEvent>> {
        return responseDataObservable.hide()
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }

    fun checkData(movieTypeRequestEvent: MovieTypeRequestEvent): Observable<MovieTypeResponseEvent> {
        return movieRepository.getPopularMovies(movieTypeRequestEvent)
            .doOnNext { Log.e(TAG, "${it.type} ${it.result.size}") }
    }

}