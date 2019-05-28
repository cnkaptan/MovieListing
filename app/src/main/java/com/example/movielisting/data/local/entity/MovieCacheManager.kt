package com.example.movielisting.data.local.entity

import com.example.movielisting.data.remote.model.MovieApiResponse

class MovieCacheManager {
    val results = mutableListOf<MovieEntity>()
    private var currentPage = -1L
    var totalPage = -1L
    var hasNext = true

    fun addDatas(apiResponse: MovieApiResponse){
        currentPage = apiResponse.page
        totalPage = apiResponse.total_pages
        hasNext = totalPage > currentPage
        results.addAll(apiResponse.results)
    }
}