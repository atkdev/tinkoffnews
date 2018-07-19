package com.tinkoff.news.features.newslist.domain

import com.tinkoff.news.domain.KeyValueCache
import com.tinkoff.news.features.newslist.domain.model.NewsListItem
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NewsListInteractorImpl(
        private val repository: NewsListRepository,
        private val cache: KeyValueCache<List<NewsListItem>>
) : NewsListInteractor {

    companion object {
        private const val NEW_LIST_CACHE_KEY = "newslist"
    }

    override fun getNewsList(): Single<List<NewsListItem>> =
            repository.getNewsList()
                    .doOnSuccess {
                        cache.set(NEW_LIST_CACHE_KEY, it)
                    }
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())


    override fun getCachedNewsList(): Maybe<List<NewsListItem>> =
            Maybe.create<List<NewsListItem>> {
                val newsList = cache.get(NEW_LIST_CACHE_KEY)
                if (newsList != null) {
                    it.onSuccess(newsList)
                }
                it.onComplete()
            }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

}