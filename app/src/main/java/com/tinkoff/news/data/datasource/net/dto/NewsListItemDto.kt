package com.tinkoff.news.data.datasource.net.dto

import com.google.gson.annotations.SerializedName

class NewsListItemDto(
        @SerializedName("id") val id: String,
        @SerializedName("text") val text: String,
        @SerializedName("publicationDate") val publicationDate: DateDto
)