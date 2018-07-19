package com.tinkoff.news.features.newscontent.domain

import com.tinkoff.news.features.newscontent.domain.model.NewsContent
import io.reactivex.Maybe
import io.reactivex.Single

interface NewsContentInteractor {
    fun getNewsContent(): Single<NewsContent>
    fun getCachedNewsContent(): Maybe<NewsContent>
}