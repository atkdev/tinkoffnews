package com.tinkoff.news.data.repository

import com.tinkoff.news.data.datasource.net.NewsApi
import com.tinkoff.news.features.newscontent.domain.NewsContentRepository
import com.tinkoff.news.features.newscontent.domain.model.NewsContent
import com.tinkoff.news.features.newslist.domain.NewsListRepository
import com.tinkoff.news.features.newslist.domain.model.NewsListItem
import io.reactivex.Single
import java.util.*


class NewsRepositoryImpl(
        private val api: NewsApi
) : NewsListRepository, NewsContentRepository {

    override fun getNewsList(): Single<List<NewsListItem>> =
            api.getNewsList().map {
                it.payload?.map {
                    NewsListItem(it.id, it.text, Date(it.publicationDate.milliseconds))
                }
            }


    override fun getNewsContent(newsId: String): Single<NewsContent> =
            api.getNewsContent(newsId).map {
                with(it.payload!!) {
                    NewsContent(text)
                }
            }

}