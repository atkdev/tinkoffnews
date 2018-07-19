package com.tinkoff.news.features.newslist.domain.model

import java.util.*

data class NewsListItem(
        val id: String,
        val text: String,
        val publicationDate: Date
)
