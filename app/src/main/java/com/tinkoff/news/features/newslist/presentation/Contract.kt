package com.tinkoff.news.features.newslist.presentation

import android.arch.lifecycle.LiveData
import com.tinkoff.news.features.newslist.domain.model.NewsListItem
import com.tinkoff.news.utils.presentation.DataLoadingViewActionHandler


///////////////////////////////////////////////////////////////////////////
// ViewModel
///////////////////////////////////////////////////////////////////////////
sealed class NewsListViewState {
    class Loading : NewsListViewState()
    class LoadingFailed : NewsListViewState()
    class Data(val newsList: List<NewsListItem>) : NewsListViewState()
}

sealed class NewsListViewModelEvent {
    class LoadingFinished: NewsListViewModelEvent()
}

interface NewsListViewModel {
    val state: LiveData<NewsListViewState>
    val event: LiveData<NewsListViewModelEvent>
}


///////////////////////////////////////////////////////////////////////////
// Presenter
///////////////////////////////////////////////////////////////////////////
interface NewsListPresenter: DataLoadingViewActionHandler {
    fun didClickNewsAt(index: Int)
}


///////////////////////////////////////////////////////////////////////////
// Router
///////////////////////////////////////////////////////////////////////////

sealed class NewsListRouterAction {
    class OpenNews(val news: NewsListItem): NewsListRouterAction()
}

interface NewsListRouter {
    val routerAction: LiveData<NewsListRouterAction>
}