package com.example.movielisting.data.local.entity

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

open class Entity

data class MovieEntity(
    @SerializedName("id")
    var id: Long,

    @SerializedName(value = "header", alternate = ["title", "name"])
    val header: String,

    @SerializedName(value = "original_title")
    val originalTitle: String,

    @SerializedName(value = "original_language")
    val originalLanguage: String,

    @SerializedName("adult")
    val adult: Boolean,

    @SerializedName("vote_count")
    val voteCount: Int,

    @SerializedName("popularity")
    val popularity: String,

    @SerializedName("poster_path")
    var posterPath: String?,

    @SerializedName(value = "description", alternate = ["overview", "synopsis"])
    var description: String?,

    @SerializedName("release_date")
    var releaseDate: String?,

    @SerializedName("runtime")
    var runTime: Long,

    @SerializedName("backdrop_path")
    var backdropPath: String?,

    @SerializedName("vote_average")
    var voteAverage: String,

    var status: String?
) : Parcelable, Entity() {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    fun getFormattedPosterPath(): String? {
        if (posterPath != null && !posterPath!!.startsWith("http")) {
            posterPath = String.format("https://image.tmdb.org/t/p/w185%s", posterPath)
        }
        return posterPath
    }

    fun getFormattedBackdropPath(): String? {
        if (backdropPath != null && !backdropPath!!.startsWith("http")) {
            backdropPath = String.format("http://image.tmdb.org/t/p/w1280%s", backdropPath)
        }
        return backdropPath
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(header)
        parcel.writeString(originalTitle)
        parcel.writeString(originalLanguage)
        parcel.writeByte(if (adult) 1 else 0)
        parcel.writeInt(voteCount)
        parcel.writeString(popularity)
        parcel.writeString(posterPath)
        parcel.writeString(description)
        parcel.writeString(releaseDate)
        parcel.writeLong(runTime)
        parcel.writeString(backdropPath)
        parcel.writeString(voteAverage)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovieEntity> {
        override fun createFromParcel(parcel: Parcel): MovieEntity {
            return MovieEntity(parcel)
        }

        override fun newArray(size: Int): Array<MovieEntity?> {
            return arrayOfNulls(size)
        }
    }
}