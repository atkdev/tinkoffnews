package com.tinkoff.news.data.datasource.net.dto

import com.google.gson.annotations.SerializedName

class ResponseDto<T> (
        @SerializedName("payload") val payload: T?
)