package com.tinkoff.news.app

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.LifecycleOwner
import com.google.gson.reflect.TypeToken
import com.tinkoff.news.data.cache.CacheObjectMapper
import com.tinkoff.news.data.cache.CacheObjectMapperImpl
import com.tinkoff.news.data.cache.KeyValueCacheImpl
import com.tinkoff.news.data.datasource.factory.RetrofitFactory
import com.tinkoff.news.data.datasource.net.NewsApi
import com.tinkoff.news.data.repository.NewsRepositoryImpl
import com.tinkoff.news.domain.KeyValueCache
import com.tinkoff.news.features.newscontent.domain.NewsContentInteractor
import com.tinkoff.news.features.newscontent.domain.NewsContentInteractorImpl
import com.tinkoff.news.features.newscontent.domain.NewsContentRepository
import com.tinkoff.news.features.newscontent.domain.model.NewsContent
import com.tinkoff.news.features.newscontent.presentation.*
import com.tinkoff.news.features.newslist.domain.NewsListInteractor
import com.tinkoff.news.features.newslist.domain.NewsListInteractorImpl
import com.tinkoff.news.features.newslist.domain.NewsListRepository
import com.tinkoff.news.features.newslist.domain.model.NewsListItem
import com.tinkoff.news.features.newslist.presentation.NewsListPresenter
import com.tinkoff.news.features.newslist.presentation.NewsListRouter
import com.tinkoff.news.features.newslist.presentation.NewsListViewModel
import com.tinkoff.news.features.newslist.presentation.NewsListViewModelImpl
import com.tinkoff.news.utils.presentation.BaseViewModel
import org.koin.android.architecture.ext.getViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.get
import org.koin.android.ext.android.startKoin
import org.koin.dsl.context.ParameterProvider
import org.koin.dsl.module.applicationContext

class TinkoffNewsApp : Application() {

    override fun onCreate() {

        super.onCreate()

        startKoin(modules = listOf(baseModule, newsListModule, newsContentModule))
    }


    ///////////////////////////////////////////////////////////////////////////
    // Koin di modules
    ///////////////////////////////////////////////////////////////////////////

    private val baseModule = applicationContext {
        bean { RetrofitFactory.createRetrofit().create(NewsApi::class.java) }
        bean("cachePath") { this@TinkoffNewsApp.cacheDir }
    }


    /* Helper functions */
    private fun ParameterProvider.activity(): LifecycleOwner = this["activity"]

    private inline fun <reified T : BaseViewModel> provideViewModel(params: ParameterProvider, observeLifeCycle: Boolean = false): T {
        val viewModel: T = params.activity().getViewModel(parameters = { params.values })
        if (observeLifeCycle) {
            params.activity().lifecycle.addObserver(viewModel)
        }
        return viewModel
    }


    private val newsListModule = applicationContext {

        fun getViewModel(params: ParameterProvider, observeLifeCycle: Boolean = false): NewsListViewModelImpl =
                provideViewModel(params, observeLifeCycle)

        viewModel { NewsListViewModelImpl(get(parameters = { it.values })) }

        factory { params -> getViewModel(params, true) as NewsListViewModel }
        factory { params -> getViewModel(params) as NewsListPresenter }
        factory { params -> getViewModel(params) as NewsListRouter }

        val moduleName = "newsList"

        factory { NewsListInteractorImpl(get(), get(moduleName)) as NewsListInteractor }

        factory { NewsRepositoryImpl(get()) as NewsListRepository }
        factory(moduleName) { KeyValueCacheImpl<List<NewsListItem>>(get("cachePath"), get(moduleName)) as KeyValueCache<List<NewsListItem>> }
        factory(moduleName) { CacheObjectMapperImpl<List<NewsListItem>>(object : TypeToken<List<NewsListItem>>() {}.type)  as CacheObjectMapper<List<NewsListItem>> }
    }

    private val newsContentModule = applicationContext {

        fun getViewModel(params: ParameterProvider, observeLifeCycle: Boolean = false): NewsContentViewModelImpl = provideViewModel(params, observeLifeCycle)

        viewModel { NewsContentViewModelImpl(get(parameters = { it.values })) }

        factory { getViewModel(it, true) as NewsContentViewModel }
        factory { getViewModel(it) as NewsContentPresenter }
        factory { getViewModel(it) as NewsContentRouter }

        val moduleName = "newsContent"

        factory {
            val newsId = (it.activity() as NewsContentActivity).getNewsId()
            NewsContentInteractorImpl(newsId, get(), get(moduleName)) as NewsContentInteractor
        }

        factory { NewsRepositoryImpl(get()) as NewsContentRepository }
        factory(moduleName) { KeyValueCacheImpl<NewsContent>(get("cachePath"), get(moduleName)) as KeyValueCache<NewsContent> }
        factory(moduleName) { CacheObjectMapperImpl<NewsContent>(object : TypeToken<NewsContent>() {}.type) as CacheObjectMapper<NewsContent> }
    }
}

inline fun <reified T> Activity.inject(parameters: Map<String, Any>? = null): Lazy<T> =
        lazy { get<T>(parameters = { (parameters ?: mapOf()).toMutableMap().also { it["activity"] = this } }) }



