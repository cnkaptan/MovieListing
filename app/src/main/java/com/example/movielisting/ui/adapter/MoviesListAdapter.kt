package com.example.movielisting.ui.adapter

import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.movielisting.R
import com.example.movielisting.data.Resource
import com.example.movielisting.data.local.entity.Entity
import com.example.movielisting.data.local.entity.MovieEntity
import com.example.movielisting.data.local.entity.MovieTypeResponseEvent
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MoviesListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object{
        const val LOADING_TYPE = 0
        const val SUCCESS_TYPE = 1

    }
    private var movies: MutableList<Entity> = mutableListOf()
    private val clickSubject = PublishSubject.create<MovieEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            SUCCESS_TYPE -> {
                CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_movie, parent, false))
            }
            else -> {
                LoadingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_loading, parent, false))
            }
        }
    }

    fun setItems(resource: Resource<MovieTypeResponseEvent>) {
        if (resource.isLoading){
            this.movies.add(Entity())
        }else if (resource.isSuccess && movies.size > 0){
            this.movies = this.movies.filter {
                it is MovieEntity
            }.toMutableList()
            this.movies.addAll(resource.data!!.result)
        }else{
            this.movies.addAll(resource.data!!.result)
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is MovieEntity -> SUCCESS_TYPE
            else -> LOADING_TYPE
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun getItem(position: Int): Entity {
        return movies[position]
    }

    fun getPageNum(): Long{
        return (itemCount / 20).toLong() + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CustomViewHolder -> {
                holder.bindTo(getItem(position) as MovieEntity)
                holder.itemView.setOnClickListener {
                    clickSubject.onNext(movies[holder.adapterPosition] as MovieEntity)
                }
            }
            is LoadingViewHolder -> holder.bindTo()
        }

    }

    fun onItemClick(): Observable<MovieEntity> = clickSubject.hide()

    class CustomViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var image: ImageView = (item as ViewGroup).findViewById(R.id.image)


        fun bindTo(movie: MovieEntity) {
            Picasso.get().load(movie.getFormattedPosterPath())
                .placeholder(R.drawable.ic_placeholder)
                .into(image)
        }
    }

    class LoadingViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        var loading: ProgressBar = (item as ViewGroup).findViewById(R.id.loading)
        fun bindTo() {
            loading.visibility = View.VISIBLE
        }
    }
}