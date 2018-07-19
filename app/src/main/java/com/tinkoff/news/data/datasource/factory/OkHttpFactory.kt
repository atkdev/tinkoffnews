package com.tinkoff.news.data.datasource.factory

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class OkHttpFactory {
    companion object {
        fun createHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
        }
    }
}