package com.tinkoff.news.features.newslist.domain

import com.tinkoff.news.features.newslist.domain.model.NewsListItem
import io.reactivex.Maybe
import io.reactivex.Single

interface NewsListInteractor {

    fun getNewsList(): Single<List<NewsListItem>>
    fun getCachedNewsList(): Maybe<List<NewsListItem>>

}