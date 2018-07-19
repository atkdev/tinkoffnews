package com.tinkoff.news.features.newscontent.presentation

import android.arch.lifecycle.LiveData
import com.tinkoff.news.features.newscontent.domain.model.NewsContent
import com.tinkoff.news.utils.presentation.DataLoadingViewActionHandler


///////////////////////////////////////////////////////////////////////////
// ViewModel
///////////////////////////////////////////////////////////////////////////
sealed class NewsContentViewState {
    class Loading : NewsContentViewState()
    class LoadingFailed : NewsContentViewState()
    class Data(val newsContent: NewsContent) : NewsContentViewState()
}

interface NewsContentViewModel {
    val state: LiveData<NewsContentViewState>
}


///////////////////////////////////////////////////////////////////////////
// Presenter
///////////////////////////////////////////////////////////////////////////
interface NewsContentPresenter: DataLoadingViewActionHandler {
    fun didClickClose()
}


///////////////////////////////////////////////////////////////////////////
// Router
///////////////////////////////////////////////////////////////////////////

sealed class NewsContentRouterAction {
    class Close: NewsContentRouterAction()
}

interface NewsContentRouter {
    val routerAction: LiveData<NewsContentRouterAction>
}