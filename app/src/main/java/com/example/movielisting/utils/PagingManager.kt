package com.example.movielisting.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movielisting.data.repository.ListType

class PagingManager(private val listener: () -> Unit) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

        if (linearLayoutManager.itemCount <= linearLayoutManager.findLastVisibleItemPosition() + 2) {
            listener()
        }
    }
}