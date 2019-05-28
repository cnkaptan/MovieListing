package com.example.movielisting.ui.adapter

import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.movielisting.R
import com.example.movielisting.data.local.entity.MovieEntity
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MoviesListAdapter : RecyclerView.Adapter<MoviesListAdapter.CustomViewHolder>() {
    private var movies: MutableList<MovieEntity> = mutableListOf()
    private val clickSubject = PublishSubject.create<MovieEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_movie, parent, false)
        return CustomViewHolder(view)
    }

    fun setItems(movies: List<MovieEntity>) {
        val startPosition = this.movies.size
        this.movies.addAll(movies)
        notifyItemRangeChanged(startPosition, movies.size)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun getItem(position: Int): MovieEntity {
        return movies[position]
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bindTo(getItem(position))
        holder.itemView.setOnClickListener {
            clickSubject.onNext(movies[holder.adapterPosition])
        }
    }

    fun onItemClick(): Observable<MovieEntity> = clickSubject.hide()

    class CustomViewHolder(private val item: View) : RecyclerView.ViewHolder(item) {
        var image: ImageView = (item as ViewGroup).findViewById(R.id.image)


        fun bindTo(movie: MovieEntity) {
            Picasso.get().load(movie.getFormattedPosterPath())
                .placeholder(R.drawable.ic_placeholder)
                .into(image)
        }
    }

}