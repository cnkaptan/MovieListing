package com.example.movielisting.utils

import com.example.movielisting.data.local.entity.MovieEntity
import com.example.movielisting.data.remote.api.MovieApiService
import com.example.movielisting.data.remote.model.MovieApiResponse
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class MovieListPagingManager(private val pageApifunc: (Long) -> Single<MovieApiResponse>) : PagingManager() {
    private val paginationRequestSubject = BehaviorSubject.create<MovieApiResponse>()
    private val compositeDisposable = CompositeDisposable()

    init {
        loadNextData()
    }

    override fun loadNextData() {
        val hasNext = hasNext()
        if (!loading && hasNext) {
            val nextPage = calculateNextPage()
            compositeDisposable.add(
                pageApifunc(nextPage)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe { loading = true }
                    .toObservable()
                    .subscribe({
                        loading = false
                        paginationRequestSubject.onNext(it)
                    }, {
                        loading = false
                        paginationRequestSubject.onError(it)
                    })
            )
        }
    }

    override fun hasNext(): Boolean {
        if (paginationRequestSubject.hasValue()) {
            val lastResponse = paginationRequestSubject.value!!
            return lastResponse.total_pages > lastResponse.page
        }
        return true
    }

    override fun calculateNextPage(): Long {
        return if (paginationRequestSubject.hasValue()) {
            paginationRequestSubject.value!!.page + 1
        } else {
            1
        }
    }

    fun getLatestDatas(): Observable<List<MovieEntity>> {
        return paginationRequestSubject.hide()
            .map { it.results }
    }

    fun clear() {
        compositeDisposable.clear()
    }

}