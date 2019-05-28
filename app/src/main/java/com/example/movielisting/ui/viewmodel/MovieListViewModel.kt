package com.example.movielisting.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movielisting.data.local.entity.MovieEntity
import com.example.movielisting.data.remote.api.MovieApiService
import com.example.movielisting.data.repository.MovieRepository
import com.example.movielisting.utils.MovieListPagingManager
import com.example.movielisting.utils.PagingManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MovieListViewModel @Inject constructor(movieApiService: MovieApiService) : ViewModel() {

    private val popularMoviesLD = MutableLiveData<List<MovieEntity>>()
    private val topRatedMoviesLD = MutableLiveData<List<MovieEntity>>()
    private val upcomingdMoviesLD = MutableLiveData<List<MovieEntity>>()

    private val movieRepository: MovieRepository = MovieRepository(movieApiService)

    private val subscriptions = CompositeDisposable()
    private val pmPagingManager = MovieListPagingManager(movieApiService::fetchMoviesPopular)
    private val trPagingManager = MovieListPagingManager(movieApiService::fetchMoviesTopRated)
    private val ucPagingManager = MovieListPagingManager(movieApiService::fetchMoviesUpcoming)

    fun getPopularMoviesLiveData() = popularMoviesLD
    fun getTopRatedMoviesLiveData() = topRatedMoviesLD
    fun getUpcomingMoviesLiveData() = upcomingdMoviesLD

    fun getpmPagingManager(): PagingManager = pmPagingManager
    fun gettrPagingManager(): PagingManager = trPagingManager
    fun getucPagingManager(): PagingManager = ucPagingManager

    fun connected() {
        subscriptions.add(
            pmPagingManager.getLatestDatas()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    popularMoviesLD.postValue(it)
                },{
                    Log.e("MovieListViewModel", it.message, it)
                })
        )

        subscriptions.add(
            trPagingManager.getLatestDatas()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        topRatedMoviesLD.postValue(it)
                    },
                    {
                        Log.e("MovieListViewModel", it.message, it)
                    })
        )

        subscriptions.add(
            ucPagingManager.getLatestDatas()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        upcomingdMoviesLD.postValue(it)
                    },
                    {
                        Log.e("MovieListViewModel", it.message, it)
                    })
        )
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
        pmPagingManager.clear()
        trPagingManager.clear()
        ucPagingManager.clear()
    }

}