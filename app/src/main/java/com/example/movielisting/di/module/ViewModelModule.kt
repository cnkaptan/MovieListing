package com.example.movielisting.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movielisting.di.ViewModelKey
import com.example.movielisting.factory.ViewModelFactory
import com.example.movielisting.ui.viewmodel.MovieListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule{

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    protected abstract fun  movieListViewModel(moviesListViewModel: MovieListViewModel): ViewModel
}
