package com.tinkoff.news.features.newslist.domain

import com.tinkoff.news.features.newslist.domain.model.NewsListItem
import io.reactivex.Single

interface NewsListRepository {

    fun getNewsList(): Single<List<NewsListItem>>
}