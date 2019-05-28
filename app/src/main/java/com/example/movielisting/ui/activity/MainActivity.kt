package com.example.movielisting.ui.activity

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import butterknife.BindView
import butterknife.ButterKnife
import com.example.movielisting.R
import com.example.movielisting.data.local.entity.MovieEntity
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Nullable
    @BindView(R.id.detail_container)
    lateinit var detailContainer: FrameLayout

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): DispatchingAndroidInjector<Fragment> {
        return dispatchingAndroidInjector
    }

    private val isLargeScreen: Boolean by lazy {
        resources.getBoolean(R.bool.is_large_screen)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        if (isLargeScreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        supportFragmentManager.commit {
            add(R.id.fragment_container, MovieListFragment())
        }
    }


    fun openDetails(movie: MovieEntity) {
        if (isLargeScreen) {
            supportFragmentManager.commit {
                add(R.id.detail_container, MovieDetailsFragment.newInstance(movie))
            }
            detailContainer.visibility = View.VISIBLE
            return
        }
        supportFragmentManager.commit {
            replace(R.id.fragment_container, MovieDetailsFragment.newInstance(movie))
            addToBackStack(null)
        }
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.backStackEntryCount
        if (fragments == 0) {
            finish()
        } else {
            if (supportFragmentManager.backStackEntryCount >= 1) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }
}