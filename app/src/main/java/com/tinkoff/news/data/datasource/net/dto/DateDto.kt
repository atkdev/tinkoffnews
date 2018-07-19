package com.tinkoff.news.data.datasource.net.dto

import com.google.gson.annotations.SerializedName

class DateDto(
        @SerializedName("milliseconds") val milliseconds: Long
)