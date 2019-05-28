package com.example.movielisting.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.movielisting.R
import com.example.movielisting.ui.adapter.MoviesListAdapter
import com.example.movielisting.ui.viewmodel.MovieListViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MovieListFragment : Fragment() {

    @BindView(R.id.movies_list_popular)
    lateinit var moviesListPopular: RecyclerView

    @BindView(R.id.movies_list_top_rated)
    lateinit var moviesListTopRated: RecyclerView

    @BindView(R.id.movies_list_upcoming)
    lateinit var moviesListUpcoming: RecyclerView

    @BindView(R.id.root_view)
    lateinit var loaderRoot: ViewGroup

    @BindView(R.id.emptyContainer)
    lateinit var emptyContainer: ViewGroup

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var moviesListViewModel: MovieListViewModel
    private lateinit var popularMoviesListAdapter: MoviesListAdapter
    private lateinit var topRatedMoviesListAdapter: MoviesListAdapter
    private lateinit var upcomingMoviesListAdapter: MoviesListAdapter


    private var subscriptions = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)
        ButterKnife.bind(this,view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
    }

    override fun onStart() {
        super.onStart()
        moviesListViewModel.connected()
    }

    private fun initialiseView() {
        popularMoviesListAdapter = MoviesListAdapter()
        moviesListPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListPopular.adapter = popularMoviesListAdapter
        moviesListPopular.addOnScrollListener(moviesListViewModel.getpmPagingManager())

        subscriptions.add(
            popularMoviesListAdapter.onItemClick()
                .subscribe({
                    (activity as MainActivity).openDetails(it)
                },{
                    Log.e("MovieListFragment", it.message, it)
                })
        )


        topRatedMoviesListAdapter = MoviesListAdapter()
        moviesListTopRated.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListTopRated.adapter = topRatedMoviesListAdapter
        moviesListTopRated.addOnScrollListener(moviesListViewModel.gettrPagingManager())

        subscriptions.add(
            topRatedMoviesListAdapter.onItemClick()
                .subscribe({
                    (activity as MainActivity).openDetails(it)
                },{
                    Log.e("MovieListFragment", it.message, it)
                })
        )

        upcomingMoviesListAdapter = MoviesListAdapter()
        moviesListUpcoming.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListUpcoming.adapter = upcomingMoviesListAdapter
        moviesListUpcoming.addOnScrollListener(moviesListViewModel.getucPagingManager())

        subscriptions.add(
            topRatedMoviesListAdapter.onItemClick()
                .subscribe({
                    (activity as MainActivity).openDetails(it)
                },{
                    Log.e("MovieListFragment", it.message, it)
                })
        )
    }

    private fun initialiseViewModel() {
        moviesListViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieListViewModel::class.java)
        moviesListViewModel.getPopularMoviesLiveData().observe(this, Observer { resource ->
            if (resource != null && !resource.isEmpty()) {
               popularMoviesListAdapter.setItems(resource)
            }
        })

        moviesListViewModel.getTopRatedMoviesLiveData().observe(this, Observer { resource ->
            if (resource != null && !resource.isEmpty()) {
                topRatedMoviesListAdapter.setItems(resource)
            }
        })

        moviesListViewModel.getUpcomingMoviesLiveData().observe(this, Observer { resource ->
            if (resource != null && !resource.isEmpty()) {
                upcomingMoviesListAdapter.setItems(resource)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscriptions.clear()
    }
}