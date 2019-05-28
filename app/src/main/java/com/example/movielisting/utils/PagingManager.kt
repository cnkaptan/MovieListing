package com.example.movielisting.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PagingManager: RecyclerView.OnScrollListener() {
    var loading = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

        if (!loading && linearLayoutManager.itemCount <= linearLayoutManager.findLastVisibleItemPosition() + 2){
            loadNextData()
        }
    }

    abstract fun loadNextData()

    abstract fun hasNext(): Boolean

    abstract fun calculateNextPage(): Long
}