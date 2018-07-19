package com.tinkoff.news.features.newslist.presentation

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.OnLifecycleEvent
import com.tinkoff.news.features.newslist.domain.NewsListInteractor
import com.tinkoff.news.utils.presentation.ActionLiveData
import com.tinkoff.news.utils.presentation.BaseViewModel
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable



class NewsListViewModelImpl(
        private val interactor: NewsListInteractor
) : BaseViewModel(),
        NewsListViewModel,
        NewsListPresenter,
        NewsListRouter {

    override val state = MutableLiveData<NewsListViewState>()
    override val event = ActionLiveData<NewsListViewModelEvent>()
    override val routerAction = MutableLiveData<NewsListRouterAction>()


    private var disposables = CompositeDisposable()


    ///////////////////////////////////////////////////////////////////////////
    // NewsListPresenter
    ///////////////////////////////////////////////////////////////////////////
    override fun tryReloadData() {

        if(state.value !is NewsListViewState.Data) {
            state.value = NewsListViewState.Loading()
        }

        disposables.add(
                interactor.getCachedNewsList()
                        .doOnSuccess {
                            state.value = NewsListViewState.Data(it)
                        }
                        .onErrorResumeNext{ err: Throwable ->
                            err.printStackTrace()
                            Maybe.empty()
                        }
                        .isEmpty.flatMap {
                            interactor.getNewsList()
                        }
                        .doAfterTerminate {
                            event.sendAction(NewsListViewModelEvent.LoadingFinished())
                        }
                        .subscribe(
                        {
                            state.value = NewsListViewState.Data(it)
                        },
                        {
                            it.printStackTrace()
                            if(state.value !is NewsListViewState.Data) {
                                state.value = NewsListViewState.LoadingFailed()
                            }
                        }
                )
        )
    }

    override fun didClickNewsAt(index: Int) {
        val listItem = (state.value as NewsListViewState.Data).newsList[index]
        routerAction.postValue(NewsListRouterAction.OpenNews(listItem))
    }


    ///////////////////////////////////////////////////////////////////////////
    // LifecycleObserver
    ///////////////////////////////////////////////////////////////////////////
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        tryReloadData()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposables.dispose()
    }
}