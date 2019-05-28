package com.example.movielisting.di.module

import com.example.movielisting.ui.activity.MovieDetailsFragment
import com.example.movielisting.ui.activity.MovieListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMovieListFragment(): MovieListFragment

    @ContributesAndroidInjector
    abstract fun contributeMovieDetailFragment(): MovieDetailsFragment
}
