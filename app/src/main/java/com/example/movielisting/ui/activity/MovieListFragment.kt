package com.example.movielisting.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.movielisting.R
import com.example.movielisting.data.Resource
import com.example.movielisting.data.local.entity.*
import com.example.movielisting.ui.adapter.MoviesListAdapter
import com.example.movielisting.ui.viewmodel.MovieListViewModel
import com.example.movielisting.utils.PagingManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class MovieListFragment : Fragment() {

    private lateinit var popularPM: PagingManager
    private lateinit var topRatedPM: PagingManager
    private lateinit var upcomingPM: PagingManager

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
    private val notifierSubject = PublishSubject.create<MovieTypeRequestEvent>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_movie_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseViewModel()
        initialiseView()
    }

    override fun onStart() {
        super.onStart()

    }

    private fun initialiseView() {
        popularMoviesListAdapter = MoviesListAdapter()
        moviesListPopular.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListPopular.adapter = popularMoviesListAdapter
        popularPM = PagingManager {
            notifyAskData(MovieTypeRequestEvent(POPULAR, popularMoviesListAdapter.getPageNum()))
        }
        moviesListPopular.addOnScrollListener(popularPM)

        topRatedMoviesListAdapter = MoviesListAdapter()
        moviesListTopRated.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListTopRated.adapter = topRatedMoviesListAdapter
        topRatedPM = PagingManager {
            notifyAskData(MovieTypeRequestEvent(TOP_RATED, topRatedMoviesListAdapter.getPageNum()))
        }
        moviesListTopRated.addOnScrollListener(topRatedPM)


        upcomingMoviesListAdapter = MoviesListAdapter()
        moviesListUpcoming.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moviesListUpcoming.adapter = upcomingMoviesListAdapter
        upcomingPM = PagingManager {
            notifyAskData(MovieTypeRequestEvent(UPCOMING, upcomingMoviesListAdapter.getPageNum()))
        }
        moviesListUpcoming.addOnScrollListener(upcomingPM)

        moviesListViewModel.connected(notifierSubject.hide())

        subscriptions.add(
            Observable.merge(
                popularMoviesListAdapter.onItemClick(),
                topRatedMoviesListAdapter.onItemClick(),
                upcomingMoviesListAdapter.onItemClick()
            )
                .firstOrError()
                .subscribe { entity ->
                    (activity as MainActivity).openDetails(entity)
                }
        )
    }

    private fun notifyAskData(reqTypeEvent: MovieTypeRequestEvent) {
        notifierSubject.onNext(reqTypeEvent)
    }


    private fun updateRecyclerView(resource: Resource<MovieTypeResponseEvent>) {
        when (resource.data!!.type) {
            POPULAR -> {
                popularMoviesListAdapter.setItems(resource)
                popularPM.loading = resource.isLoading
            }
            TOP_RATED -> {
                topRatedMoviesListAdapter.setItems(resource)
                topRatedPM.loading = resource.isLoading
            }
            UPCOMING -> {
                upcomingMoviesListAdapter.setItems(resource)
                upcomingPM.loading = resource.isLoading
            }
        }
    }

    private fun initialiseViewModel() {
        moviesListViewModel = ViewModelProviders.of(this, viewModelFactory).get(MovieListViewModel::class.java)
        subscriptions.add(
            moviesListViewModel.getScrollResponses().subscribe {
                updateRecyclerView(it)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscriptions.clear()
    }
}