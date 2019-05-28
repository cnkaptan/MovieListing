package com.example.movielisting

import android.app.Activity
import android.app.Application
import android.util.Log
import com.example.movielisting.di.component.DaggerAppComponent
import com.squareup.picasso.LruCache
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject
import dagger.android.HasActivityInjector
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import android.os.Debug.getMemoryInfo
import android.app.ActivityManager
import android.content.Context


class AppController : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)


        val requestTransformer = Picasso.RequestTransformer { request ->
            Log.d("image request", request.toString())
            request
        }

        val builder = Picasso.Builder(this)
            .memoryCache(LruCache(getBytesForMemCache(12)))
            .requestTransformer(requestTransformer)

        Picasso.setSingletonInstance(builder.build())
    }

    //returns the given percentage of available memory in bytes
    private fun getBytesForMemCache(percent: Int): Int {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        val availableMemory = mi.availMem.toDouble()
        return (percent * availableMemory / 100).toInt()
    }
}