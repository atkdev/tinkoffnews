package com.tinkoff.news.app

import android.app.Application
import com.tinkoff.news.di.createBaseModule
import com.tinkoff.news.di.newsContentModule
import com.tinkoff.news.di.newsListModule
import org.koin.android.ext.android.startKoin

class TinkoffNewsApp : Application() {

    override fun onCreate() {

        super.onCreate()

        startKoin(modules = listOf(createBaseModule(this), newsListModule, newsContentModule))
    }

}




