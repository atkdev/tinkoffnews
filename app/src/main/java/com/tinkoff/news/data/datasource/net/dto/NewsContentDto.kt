package com.tinkoff.news.data.datasource.net.dto

import com.google.gson.annotations.SerializedName

class NewsContentDto(
        @SerializedName("content") val text: String
)