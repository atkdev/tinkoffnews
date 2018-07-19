package com.tinkoff.news.features.newscontent.domain

import com.tinkoff.news.domain.KeyValueCache
import com.tinkoff.news.features.newscontent.domain.model.NewsContent
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NewsContentInteractorImpl(
        private val newsId: String,
        private val repository: NewsContentRepository,
        private val cache: KeyValueCache<NewsContent>
): NewsContentInteractor {

    private val cacheKey = "NEWS_CONTENT_$newsId"

    override fun getNewsContent(): Single<NewsContent> =
        repository.getNewsContent(newsId)
                .doOnSuccess { cache.set(cacheKey, it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    override fun getCachedNewsContent(): Maybe<NewsContent> =
            Maybe.create<NewsContent> {
                val newsList = cache.get(cacheKey)
                if (newsList != null) {
                    it.onSuccess(newsList)
                }
                it.onComplete()
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())



}