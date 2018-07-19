package com.tinkoff.news.data.datasource.net

import com.tinkoff.news.data.datasource.net.dto.NewsContentDto
import com.tinkoff.news.data.datasource.net.dto.NewsListItemDto
import com.tinkoff.news.data.datasource.net.dto.ResponseDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v1/news")
    fun getNewsList(): Single<ResponseDto<List<NewsListItemDto>>>


    @GET("v1/news_content")
    fun getNewsContent(@Query("id") newsId: String): Single<ResponseDto<NewsContentDto>>

}