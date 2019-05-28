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
import com.example.movielisting.data.local.entity.MovieEntity
import com.example.movielisting.data.repository.ListType
import com.example.movielisting.data.repository.POPULAR
import com.example.movielisting.data.repository.TOP_RATED
import com.example.movielisting.data.repository.UPCOMING
import com.example.movielisting.ui.adapter.MoviesListAdapter
import com.example.movielisting.ui.viewmodel.MovieListViewModel
import com.example.movielisting.utils.PagingManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MovieListFragment : Fragment() {

    @BindView(R.id.movies_list_popular)
    lateinit var moviesListPopular: RecyclerView

    @BindView(R.id.movies_list_top_rated)
    lateinit var moviesListTopRated: RecyclerView

    @BindView(R.id.movies_list_upcoming)
    lateinit var moviesListUpcoming: RecyclerView


    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var moviesListViewModel: MovieListViewModel
    private lateinit var popularMoviesListAdapter: MoviesListAdapter
    private lateinit var topRatedMoviesListAdapter: MoviesListAdapter
    private lateinit var upcomingMoviesListAdapter: MoviesListAdapter

    private val subscriptions = CompositeDisposable()
    private val notifierSubject = PublishSubject.create<Pair<ListType, Long>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        initialiseViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseView()
    }

    override fun onStart() {
        super.onStart()

    }

    private fun initialiseView() {
        popularMoviesListAdapter = MoviesListAdapter()
        moviesListPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListPopular.adapter = popularMoviesListAdapter
        moviesListPopular.addOnScrollListener(PagingManager {
            notifyAskData(POPULAR, popularMoviesListAdapter.getPageNum())
        })

        topRatedMoviesListAdapter = MoviesListAdapter()
        moviesListTopRated.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListTopRated.adapter = topRatedMoviesListAdapter
        moviesListTopRated.addOnScrollListener(PagingManager {
            notifyAskData(TOP_RATED, topRatedMoviesListAdapter.getPageNum())
        })


        upcomingMoviesListAdapter = MoviesListAdapter()
        moviesListUpcoming.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListUpcoming.adapter = upcomingMoviesListAdapter
        moviesListUpcoming.addOnScrollListener(PagingManager {
            notifyAskData(UPCOMING, upcomingMoviesListAdapter.getPageNum())
        })

        moviesListViewModel.connected(notifierSubject.hide())
    }

    private fun notifyAskData(type: ListType, pageNum: Long) {
        notifierSubject.onNext(type to pageNum)
    }


    private fun updateRecyclerView(type: ListType, result: List<MovieEntity>) {
        Log.e("MainListFragment", "updateRecyclerView: $type --> ${result.size}")
        when (type) {
            POPULAR -> popularMoviesListAdapter.setItems(result)
            TOP_RATED -> topRatedMoviesListAdapter.setItems(result)
            UPCOMING -> upcomingMoviesListAdapter.setItems(result)
        }
    }

    private fun initialiseViewModel() {
        moviesListViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieListViewModel::class.java)
        moviesListViewModel.getScrollResponses().observe(this, Observer {
            updateRecyclerView(it.first, it.second)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}