package com.example.movielisting.data.local.entity

sealed class ListType
object POPULAR : ListType()
object TOP_RATED : ListType()
object UPCOMING : ListType()