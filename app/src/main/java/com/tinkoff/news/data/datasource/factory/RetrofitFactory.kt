package com.tinkoff.news.data.datasource.factory

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitFactory {

    companion object {
        fun createRetrofit(): Retrofit = Retrofit.Builder()
                .baseUrl("https://api.tinkoff.ru/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkHttpFactory.createHttpClient())
                .build()
    }

}