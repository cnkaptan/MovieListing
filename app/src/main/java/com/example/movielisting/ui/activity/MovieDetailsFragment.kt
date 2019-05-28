package com.example.movielisting.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.movielisting.R
import com.example.movielisting.data.local.entity.MovieEntity
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import dagger.android.support.AndroidSupportInjection

import java.util.Locale
import javax.inject.Inject

class MovieDetailsFragment : Fragment() {

    private lateinit var currentMovie: MovieEntity

    @BindView(R.id.iv_backdrop)
    lateinit var mIvBackdropImage: ImageView

    @BindView(R.id.tv_movie_title)
    lateinit var mTvMovieTitle: TextView

    @BindView(R.id.tv_rating)
    lateinit var mTvMovieRating: TextView

    @BindView(R.id.tv_movie_description)
    lateinit var mTvMovieDesc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.postponeEnterTransition(activity!!)

        if (arguments != null) {
            currentMovie = arguments!!.getParcelable(ARG_MOVIE_PARAM)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_movie_details, container, false)
        ButterKnife.bind(this, view)
        updateUI()
        return view
    }

    private fun updateUI() {
        loadWithPicasso(mIvBackdropImage, currentMovie.getFormattedBackdropPath()!!)

        currentMovie.let {
            mTvMovieRating.text = it.voteAverage
            mTvMovieTitle.text = it.header
            mTvMovieDesc.text = it.description
        }
    }

    private fun loadWithPicasso(into: ImageView, url: String) {
        //if url is empty that means we don't have the url simply return.
        if (TextUtils.isEmpty(url))
            return
        //We will try to fetch offline data if it is possible.
        //If no we will fetch it from URL
        Picasso.get()
            .load(url)
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(into, object : Callback {
                override fun onSuccess() {
                    Log.w("Picasso", "$url: Image loaded from cache")
                }

                override fun onError(e: Exception) {
                    Picasso.get()
                        .load(url)
                        .into(into, object : Callback {
                            override fun onSuccess() {}

                            override fun onError(e: Exception) {
                                Log.w("Picasso", "$url: couldn't download the image.")
                            }
                        })
                }
            })
    }

    companion object {

        private const val ARG_MOVIE_PARAM = "detailedMovie"

        fun newInstance(movieParam: MovieEntity): MovieDetailsFragment {
            val fragment = MovieDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_MOVIE_PARAM, movieParam)
            fragment.arguments = args
            return fragment
        }
    }
}
