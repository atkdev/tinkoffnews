package com.tinkoff.news.features.newscontent.domain

import com.tinkoff.news.features.newscontent.domain.model.NewsContent
import io.reactivex.Single

interface NewsContentRepository {
    fun getNewsContent(newsId: String): Single<NewsContent>
}