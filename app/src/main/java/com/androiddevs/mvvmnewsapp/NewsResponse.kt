package com.androiddevs.mvvmnewsapp

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)